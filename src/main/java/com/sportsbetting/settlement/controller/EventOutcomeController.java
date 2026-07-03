package com.sportsbetting.settlement.controller;

import com.sportsbetting.settlement.dto.EventOutcome;
import com.sportsbetting.settlement.messaging.kafka.EventOutcomeProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event-outcomes")
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeController {

    private final EventOutcomeProducer eventOutcomeProducer;

    /**
     * Accepts an event outcome, publishes it to the Kafka {@code event-outcomes}
     * topic, and returns 202 Accepted with the submitted payload.
     *
     * <p>Example request:
     * <pre>
     * POST /api/event-outcomes
     * {
     *   "eventId": "event-1",
     *   "eventName": "Team A vs Team B",
     *   "eventWinnerId": "team-a"
     * }
     * </pre>
     *
     * <p>The settlement service consumes the message from the topic and
     * settles all OPEN bets for the given event asynchronously.
     */
    @PostMapping
    public ResponseEntity<EventOutcome> publishEventOutcome(
            @Valid @RequestBody EventOutcome outcome) {

        log.info("POST /api/event-outcomes — eventId={}, eventName='{}', winner='{}'",
                outcome.getEventId(), outcome.getEventName(), outcome.getEventWinnerId());

        eventOutcomeProducer.publish(outcome);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(outcome);
    }
}
