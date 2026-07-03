package com.sportsbetting.settlement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Inbound message / DTO that carries the final result of a sports event.
 * Consumed from the Kafka {@code event-outcomes} topic and also accepted
 * directly by the REST settlement endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventOutcome {

    @NotBlank(message = "eventId is required")
    private String eventId;

    @NotBlank(message = "eventName is required")
    private String eventName;

    /**
     * ID of the winning participant (team, player, outcome code …).
     * Compared against {@code Bet#eventWinnerId} to determine WON / LOST.
     */
    @NotBlank(message = "eventWinnerId is required")
    private String eventWinnerId;
}
