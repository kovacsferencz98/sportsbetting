package com.sportsbetting.settlement.messaging.rocketmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsbetting.settlement.dto.BetSettlementMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RocketMqSettlementProducerService implements RocketMqSettlementProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final String betSettlementsTopic;
    private final ObjectMapper objectMapper;

    public RocketMqSettlementProducerService(
            RocketMQTemplate rocketMQTemplate,
            ObjectMapper objectMapper,
            @Value("${app.rocketmq.bet-settlements-topic}") String betSettlementsTopic) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.objectMapper = objectMapper;
        this.betSettlementsTopic = betSettlementsTopic;
    }

    @Override
    public void publish(BetSettlementMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            rocketMQTemplate.convertAndSend(betSettlementsTopic, payload);

            log.info("RocketMQ send succeeded — topic={}, betId={}, result={}",
                    betSettlementsTopic, message.getBetId(), message.getResult());
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize BetSettlementMessage to JSON for RocketMQ. betId={}: {}",
                    message.getBetId(), ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Failed to publish BetSettlementMessage to RocketMQ topic={} for betId={}: {}",
                    betSettlementsTopic, message.getBetId(), ex.getMessage(), ex);
        }
    }
}
