package com.sportsbetting.settlement.messaging.rocketmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsbetting.settlement.dto.BetSettlementMessage;
import com.sportsbetting.settlement.service.BetSettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RocketMqSettlementConsumer {

    private final BetSettlementService betSettlementService;
    private final ObjectMapper objectMapper;

    public void onMessage(String payload) {
        if (payload == null || payload.isBlank()) {
            log.warn("Received empty RocketMQ payload — ignoring.");
            return;
        }

        log.info("Received RocketMQ settlement message: {}", payload);

        try {
            BetSettlementMessage message = objectMapper.readValue(payload, BetSettlementMessage.class);
            betSettlementService.processSettlement(message);
        } catch (JsonProcessingException ex) {
            log.error("Failed to deserialize RocketMQ payload into BetSettlementMessage: {}", payload, ex);
        }
    }
}
