package com.sportsbetting.settlement.config;

import com.sportsbetting.settlement.model.Bet;
import com.sportsbetting.settlement.model.BetStatus;
import com.sportsbetting.settlement.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final BetRepository betRepository;

    @Override
    public void run(String... args) {

        List<Bet> bets = List.of(
            bet("alice", "event-1", "match-winner", "team-a", "50.00"),
            bet("bob", "event-1", "match-winner", "team-b", "25.00"),
            bet("carol", "event-1", "match-winner", "team-c", "30.00"),

            bet("alice", "event-2", "match-winner", "team-x", "100.00"),
            bet("dave", "event-2", "match-winner", "team-y", "75.00"),

            bet("bob", "event-3", "match-winner", "team-p", "40.00"),
            bet("carol", "event-3", "match-winner", "team-q", "20.00")
        );

        betRepository.saveAll(bets);

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("  Seed data loaded — {} OPEN bets across 3 events", bets.size());
        log.info("  event-1: 3 bets");
        log.info("  event-2: 2 bets");
        log.info("  event-3: 2 bets");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private Bet bet(String userId, String eventId, String marketId,
                    String eventWinnerId, String amount) {
        return Bet.builder()
                .userId(userId)
                .eventId(eventId)
                .eventMarketId(marketId)
                .eventWinnerId(eventWinnerId)
                .betAmount(new BigDecimal(amount))
                .status(BetStatus.OPEN)
                .build();
    }
}
