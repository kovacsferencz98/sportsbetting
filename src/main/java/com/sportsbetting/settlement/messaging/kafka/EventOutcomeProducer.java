package com.sportsbetting.settlement.messaging.kafka;

import com.sportsbetting.settlement.dto.EventOutcome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.event-outcomes-topic}")
    private String eventOutcomesTopic;

    public void publish(EventOutcome outcome) {
        String key = String.valueOf(outcome.getEventId());

        log.info("Publishing EventOutcome to '{}': eventId={}, eventName='{}', winner='{}'",
                eventOutcomesTopic, outcome.getEventId(),
                outcome.getEventName(), outcome.getEventWinnerId());

        kafkaTemplate.send(eventOutcomesTopic, key, outcome)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("EventOutcome published — eventId={}, partition={}, offset={}",
                                outcome.getEventId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish EventOutcome for eventId={}: {}",
                                outcome.getEventId(), ex.getMessage(), ex);
                    }
                });
    }
}

