package com.sportsbetting.settlement.config;

import com.sportsbetting.settlement.messaging.rocketmq.RocketMqSettlementConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class RocketMqConsumerConfig {

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQPushConsumer settlementPushConsumer(
            @Value("${rocketmq.name-server}") String nameServer,
            @Value("${app.rocketmq.consumer-group:settlement-consumer-group}") String consumerGroup,
            @Value("${app.rocketmq.bet-settlements-topic}") String settlementTopic,
            RocketMqSettlementConsumer settlementConsumer) throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(nameServer);
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(settlementTopic, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            for (MessageExt message : messages) {
                String payload = new String(message.getBody(), StandardCharsets.UTF_8);
                log.info("RocketMQ raw message received — topic={}, queueId={}, msgId={}, reconsumeTimes={}",
                        message.getTopic(), message.getQueueId(), message.getMsgId(), message.getReconsumeTimes());
                settlementConsumer.onMessage(payload);
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        log.info("RocketMQ push consumer configured — group={}, topic={}, namesrv={}",
                consumerGroup, settlementTopic, nameServer);
        return consumer;
    }
}
