package com.sportsbetting.settlement.service;

import com.sportsbetting.settlement.dto.BetSettlementMessage;
import com.sportsbetting.settlement.model.Bet;
import com.sportsbetting.settlement.model.BetStatus;
import com.sportsbetting.settlement.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetSettlementService {

    private final BetRepository betRepository;

    @Transactional
    public void processSettlement(BetSettlementMessage message) {
        if (message == null) {
            log.warn("Received null BetSettlementMessage — ignoring.");
            return;
        }

        logProcessing(message);

        if (hasNoBetId(message)) {
            return;
        }

        findBet(message.getBetId())
            .ifPresent(bet -> settleIfOpen(bet, message));
    }

    private void logProcessing(BetSettlementMessage message) {
        log.info("Processing settlement message — betId={}, eventId={}, result={}, amount={}",
            message.getBetId(), message.getEventId(), message.getResult(), message.getBetAmount());
    }

    private boolean hasNoBetId(BetSettlementMessage message) {
        if (message.getBetId() != null) {
            return false;
        }

        log.warn("BetSettlementMessage has no betId — ignoring message: {}", message);
        return true;
    }

    private Optional<Bet> findBet(Long betId) {
        Optional<Bet> bet = betRepository.findById(betId);
        if (bet.isEmpty()) {
            log.warn("No bet found for betId={} — settlement message ignored", betId);
        }

        return bet;
    }

    private void settleIfOpen(Bet bet, BetSettlementMessage message) {
        if (isAlreadySettled(bet)) {
            log.info("Duplicate settlement ignored for betId={} because status is already {}",
                bet.getBetId(), bet.getStatus());
            return;
        }

        BetStatus newStatus = toBetStatus(message.getResult());
        bet.setStatus(newStatus);
        betRepository.save(bet);

        log.info("Completed settlement for betId={} -> {}", bet.getBetId(), newStatus);
    }

    private boolean isAlreadySettled(Bet bet) {
        return bet.getStatus() == BetStatus.WON || bet.getStatus() == BetStatus.LOST;
    }

    private BetStatus toBetStatus(BetSettlementMessage.Result result) {
        return result == BetSettlementMessage.Result.WON ? BetStatus.WON : BetStatus.LOST;
    }
}

