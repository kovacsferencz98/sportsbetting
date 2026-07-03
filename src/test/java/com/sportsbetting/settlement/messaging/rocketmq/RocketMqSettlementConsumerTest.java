package com.sportsbetting.settlement.messaging.rocketmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsbetting.settlement.dto.BetSettlementMessage;
import com.sportsbetting.settlement.service.BetSettlementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RocketMqSettlementConsumerTest {

    @Mock
    private BetSettlementService betSettlementService;

    private RocketMqSettlementConsumer consumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        consumer = new RocketMqSettlementConsumer(betSettlementService, objectMapper);
    }

    @Test
    void onMessage_withValidPayload_processesDeserializedSettlementMessage() throws JsonProcessingException {
        BetSettlementMessage message = BetSettlementMessage.builder()
                .betId(7L)
                .userId("alice")
                .eventId("event-1")
                .eventWinnerId("team-a")
                .betAmount(new BigDecimal("30.00"))
                .result(BetSettlementMessage.Result.WON)
                .build();

        String payload = objectMapper.writeValueAsString(message);

        consumer.onMessage(payload);

        ArgumentCaptor<BetSettlementMessage> captor = ArgumentCaptor.forClass(BetSettlementMessage.class);
        verify(betSettlementService).processSettlement(captor.capture());

        BetSettlementMessage captured = captor.getValue();
        assertThat(captured.getBetId()).isEqualTo(7L);
        assertThat(captured.getEventId()).isEqualTo("event-1");
        assertThat(captured.getResult()).isEqualTo(BetSettlementMessage.Result.WON);
    }

    @Test
    void onMessage_withBlankPayload_ignoresIt() {
        consumer.onMessage("   ");

        verifyNoInteractions(betSettlementService);
    }

    @Test
    void onMessage_withInvalidJson_logsAndSkipsProcessing() throws JsonProcessingException {
        consumer.onMessage("{not-valid-json}");

        verifyNoInteractions(betSettlementService);
    }
}

