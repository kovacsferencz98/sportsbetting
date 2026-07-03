package com.sportsbetting.settlement.messaging.kafka;

import com.sportsbetting.settlement.dto.BetSettlementMessage;
import com.sportsbetting.settlement.dto.EventOutcome;
import com.sportsbetting.settlement.messaging.rocketmq.RocketMqSettlementProducer;
import com.sportsbetting.settlement.service.BetMatchingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventResultConsumerTest {

    @Mock BetMatchingService betMatchingService;
    @Mock RocketMqSettlementProducer rocketMqSettlementProducer;

    @InjectMocks EventResultConsumer consumer;

    private static final EventOutcome OUTCOME =
            new EventOutcome("event-1", "Home vs Away", "team-home");

    private static BetSettlementMessage wonMsg() {
        return BetSettlementMessage.builder()
                .betId(1L).userId("alice").eventId("event-1")
                .eventWinnerId("team-home").betAmount(new BigDecimal("50.00"))
                .result(BetSettlementMessage.Result.WON).build();
    }

    private static BetSettlementMessage lostMsg() {
        return BetSettlementMessage.builder()
                .betId(2L).userId("bob").eventId("event-1")
                .eventWinnerId("team-home").betAmount(new BigDecimal("25.00"))
                .result(BetSettlementMessage.Result.LOST).build();
    }

    @Test
    void consume_delegatesToBetMatchingService() {
        when(betMatchingService.matchBets(OUTCOME)).thenReturn(List.of(wonMsg()));

        consumer.consume(OUTCOME);

        verify(betMatchingService).matchBets(OUTCOME);
    }

    @Test
    void consume_publishesEachMessageToRocketMQ() {
        when(betMatchingService.matchBets(OUTCOME)).thenReturn(List.of(wonMsg(), lostMsg()));

        consumer.consume(OUTCOME);

        verify(rocketMqSettlementProducer, times(2)).publish(any(BetSettlementMessage.class));
    }

    @Test
    void consume_doesNotPublish_whenNoMessages() {
        when(betMatchingService.matchBets(OUTCOME)).thenReturn(List.of());

        consumer.consume(OUTCOME);

        verifyNoInteractions(rocketMqSettlementProducer);
    }
}
