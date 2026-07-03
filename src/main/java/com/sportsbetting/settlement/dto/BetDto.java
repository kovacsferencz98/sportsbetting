package com.sportsbetting.settlement.dto;

import com.sportsbetting.settlement.model.BetStatus;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BetDto {

    private Long betId;
    private String userId;
    private String eventId;
    private String eventMarketId;
    private String eventWinnerId;
    private BigDecimal betAmount;
    private BetStatus status;
}
