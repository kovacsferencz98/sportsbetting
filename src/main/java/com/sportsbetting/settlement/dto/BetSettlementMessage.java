package com.sportsbetting.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Outbound message published to the Kafka {@code bet-settlements} topic
 * (and logged by the RocketMQ mock) once a bet has been settled.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BetSettlementMessage {

    private Long betId;

    private String userId;

    private String eventId;

    private String eventWinnerId;

    private BigDecimal betAmount;

    private Result result;

    public enum Result {
        WON,
        LOST
    }
}
