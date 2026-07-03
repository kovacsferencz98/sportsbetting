package com.sportsbetting.settlement.messaging.kafka;

import com.sportsbetting.settlement.dto.BetSettlementMessage;
import com.sportsbetting.settlement.dto.EventOutcome;
import com.sportsbetting.settlement.messaging.rocketmq.RocketMqSettlementProducer;
import com.sportsbetting.settlement.service.BetMatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventResultConsumer {

    private final BetMatchingService betMatchingService;
    private final RocketMqSettlementProducer rocketMqSettlementProducer;

    @KafkaListener(
            topics = "${app.kafka.event-outcomes-topic}",
            groupId = "settlement-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(EventOutcome outcome) {

        log.info("EventOutcome received — eventId={}, eventName='{}', winner='{}'",
                outcome.getEventId(), outcome.getEventName(), outcome.getEventWinnerId());

        List<BetSettlementMessage> messages = betMatchingService.matchBets(outcome);

        if (messages.isEmpty()) {
            log.info("No OPEN bets for eventId={} — nothing to publish.", outcome.getEventId());
            return;
        }

        log.info("{} settlement message(s) ready for eventId={}",
                messages.size(), outcome.getEventId());

        for (BetSettlementMessage msg : messages) {
            log.debug("Publishing — betId={}, userId={}, result={}",
                    msg.getBetId(), msg.getUserId(), msg.getResult());
            rocketMqSettlementProducer.publish(msg);
        }
    }
}
