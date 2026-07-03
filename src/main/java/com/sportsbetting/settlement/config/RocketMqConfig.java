package com.sportsbetting.settlement.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMqConfig {

    private static final Logger log = LoggerFactory.getLogger(RocketMqConfig.class);

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Bean
    @ConditionalOnMissingBean
    public RocketMQTemplate rocketMQTemplate() {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(5000);
        producer.setVipChannelEnabled(false);

        ResilientRocketMQTemplate rocketMQTemplate = new ResilientRocketMQTemplate();
        rocketMQTemplate.setProducer(producer);
        return rocketMQTemplate;
    }

    static class ResilientRocketMQTemplate extends RocketMQTemplate {
        @Override
        public void afterPropertiesSet() {
            try {
                super.afterPropertiesSet();
            } catch (Exception ex) {
                log.warn("RocketMQ producer could not be started yet (broker may still be coming up). Messages will be logged only until it becomes reachable. {}", ex.getMessage());
            }
        }
    }
}
