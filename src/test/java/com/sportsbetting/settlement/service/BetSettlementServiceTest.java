package com.sportsbetting.settlement.service;

import com.sportsbetting.settlement.dto.BetSettlementMessage;
import com.sportsbetting.settlement.model.Bet;
import com.sportsbetting.settlement.model.BetStatus;
import com.sportsbetting.settlement.repository.BetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetSettlementServiceTest {

    @Mock
    private BetRepository betRepository;

    @InjectMocks
    private BetSettlementService betSettlementService;

    @Test
    void processSettlement_withNullMessage_doesNothing() {
        betSettlementService.processSettlement(null);

        verifyNoInteractions(betRepository);
    }

    @Test
    void processSettlement_withoutBetId_doesNothing() {
        BetSettlementMessage message = BetSettlementMessage.builder()
                .eventId("event-1")
                .result(BetSettlementMessage.Result.WON)
                .build();

        betSettlementService.processSettlement(message);

        verifyNoInteractions(betRepository);
    }

    @Test
    void processSettlement_whenBetNotFound_doesNothing() {
        BetSettlementMessage message = BetSettlementMessage.builder()
                .betId(10L)
                .eventId("event-1")
                .result(BetSettlementMessage.Result.WON)
                .build();

        when(betRepository.findById(10L)).thenReturn(Optional.empty());

        betSettlementService.processSettlement(message);

        verify(betRepository).findById(10L);
        verify(betRepository, never()).save(any());
    }

    @Test
    void processSettlement_forAlreadySettledBet_doesNothing() {
        Bet settledBet = Bet.builder()
                .betId(10L)
                .status(BetStatus.WON)
                .build();

        BetSettlementMessage message = BetSettlementMessage.builder()
                .betId(10L)
                .eventId("event-1")
                .result(BetSettlementMessage.Result.WON)
                .build();

        when(betRepository.findById(10L)).thenReturn(Optional.of(settledBet));

        betSettlementService.processSettlement(message);

        verify(betRepository).findById(10L);
        verify(betRepository, never()).save(any());
    }

    @Test
    void processSettlement_forOpenBet_updatesStatusAndSaves() {
        Bet openBet = Bet.builder()
                .betId(11L)
                .userId("alice")
                .eventId("event-1")
                .eventMarketId("match-winner")
                .eventWinnerId("team-a")
                .betAmount(new BigDecimal("50.00"))
                .status(BetStatus.OPEN)
                .build();

        BetSettlementMessage message = BetSettlementMessage.builder()
                .betId(11L)
                .eventId("event-1")
                .eventWinnerId("team-a")
                .betAmount(new BigDecimal("50.00"))
                .result(BetSettlementMessage.Result.WON)
                .build();

        when(betRepository.findById(11L)).thenReturn(Optional.of(openBet));

        betSettlementService.processSettlement(message);

        verify(betRepository).findById(11L);
        verify(betRepository).save(openBet);
        assertThat(openBet.getStatus()).isEqualTo(BetStatus.WON);
    }
}
