package com.sportsbetting.settlement.service;

import com.sportsbetting.settlement.dto.BetSettlementMessage;
import com.sportsbetting.settlement.dto.EventOutcome;
import com.sportsbetting.settlement.model.Bet;
import com.sportsbetting.settlement.model.BetStatus;
import com.sportsbetting.settlement.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Pure matching logic: given an {@link EventOutcome}, find every OPEN bet for
 * that event and decide WON / LOST by comparing winner IDs.
 *
 * <p>No Kafka, no RocketMQ — just a database read and a projection.
 * This keeps the logic fast to unit-test without any messaging infrastructure.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BetMatchingService {

    private final BetRepository betRepository;

    /**
     * Queries all OPEN bets for {@code outcome.eventId}, compares each
     * {@code bet.eventWinnerId} against {@code outcome.eventWinnerId} (case-insensitive),
     * and returns one {@link BetSettlementMessage} per bet.
     *
     * @param outcome the event result received from the {@code event-outcomes} topic
     * @return settlement messages — empty list when no OPEN bets exist for this event
     */
    @Transactional(readOnly = true)
    public List<BetSettlementMessage> matchBets(EventOutcome outcome) {
        List<Bet> openBets = betRepository.findByEventIdAndStatus(
                outcome.getEventId(), BetStatus.OPEN);

        log.debug("matchBets: eventId={}, openBets={}", outcome.getEventId(), openBets.size());

        return openBets.stream()
                .map(bet -> toSettlementMessage(bet, outcome))
                .collect(Collectors.toList());
    }

    private BetSettlementMessage toSettlementMessage(Bet bet, EventOutcome outcome) {
        BetSettlementMessage.Result result =
                bet.getEventWinnerId().equalsIgnoreCase(outcome.getEventWinnerId())
                        ? BetSettlementMessage.Result.WON
                        : BetSettlementMessage.Result.LOST;

        return BetSettlementMessage.builder()
                .betId(bet.getBetId())
                .userId(bet.getUserId())
                .eventId(bet.getEventId())
                .eventWinnerId(outcome.getEventWinnerId())
                .betAmount(bet.getBetAmount())
                .result(result)
                .build();
    }
}
