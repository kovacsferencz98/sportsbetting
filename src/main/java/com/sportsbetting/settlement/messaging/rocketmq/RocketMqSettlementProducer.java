package com.sportsbetting.settlement.messaging.rocketmq;

import com.sportsbetting.settlement.dto.BetSettlementMessage;

public interface RocketMqSettlementProducer {

    void publish(BetSettlementMessage message);
}

