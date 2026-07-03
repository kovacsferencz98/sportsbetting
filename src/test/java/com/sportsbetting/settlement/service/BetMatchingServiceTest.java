package com.sportsbetting.settlement.service;

import com.sportsbetting.settlement.dto.BetSettlementMessage;
import com.sportsbetting.settlement.dto.EventOutcome;
import com.sportsbetting.settlement.model.Bet;
import com.sportsbetting.settlement.model.BetStatus;
import com.sportsbetting.settlement.repository.BetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BetMatchingServiceTest {

    @Mock
    private BetRepository betRepository;

    @InjectMocks
    private BetMatchingService betMatchingService;

    private Bet openBetOnHome;
    private Bet openBetOnAway;

    @BeforeEach
    void setUp() {
        openBetOnHome = Bet.builder()
                .betId(1L)
                .userId("alice")
                .eventId("event-1")
                .eventMarketId("match-winner")
                .eventWinnerId("team-home")
                .betAmount(new BigDecimal("50.00"))
                .status(BetStatus.OPEN)
                .build();

        openBetOnAway = Bet.builder()
                .betId(2L)
                .userId("bob")
                .eventId("event-1")
                .eventMarketId("match-winner")
                .eventWinnerId("team-away")
                .betAmount(new BigDecimal("25.00"))
                .status(BetStatus.OPEN)
                .build();
    }

    @Test
    void matchBets_returnsWon_whenBetWinnerIdMatchesOutcome() {
        EventOutcome outcome = new EventOutcome("event-1", "Home vs Away", "team-home");
        when(betRepository.findByEventIdAndStatus("event-1", BetStatus.OPEN))
                .thenReturn(List.of(openBetOnHome));

        List<BetSettlementMessage> messages = betMatchingService.matchBets(outcome);

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getResult()).isEqualTo(BetSettlementMessage.Result.WON);
        verify(betRepository).findByEventIdAndStatus("event-1", BetStatus.OPEN);
    }

    @Test
    void matchBets_returnsLost_whenBetWinnerIdDiffersFromOutcome() {
        EventOutcome outcome = new EventOutcome("event-1", "Home vs Away", "team-away");
        when(betRepository.findByEventIdAndStatus("event-1", BetStatus.OPEN))
                .thenReturn(List.of(openBetOnHome));

        List<BetSettlementMessage> messages = betMatchingService.matchBets(outcome);

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getResult()).isEqualTo(BetSettlementMessage.Result.LOST);
        verify(betRepository).findByEventIdAndStatus("event-1", BetStatus.OPEN);
    }

    @Test
    void matchBets_onlySelectsOpenBetsForMatchingEventId() {
        EventOutcome outcome = new EventOutcome("event-1", "Home vs Away", "team-home");

        when(betRepository.findByEventIdAndStatus("event-1", BetStatus.OPEN))
                .thenReturn(List.of(openBetOnHome, openBetOnAway));

        List<BetSettlementMessage> messages = betMatchingService.matchBets(outcome);

        assertThat(messages).hasSize(2);
        assertThat(messages)
                .extracting(BetSettlementMessage::getResult)
                .containsExactly(BetSettlementMessage.Result.WON, BetSettlementMessage.Result.LOST);
        verify(betRepository).findByEventIdAndStatus("event-1", BetStatus.OPEN);
    }
}
