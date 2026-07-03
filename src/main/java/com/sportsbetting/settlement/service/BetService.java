package com.sportsbetting.settlement.service;

import com.sportsbetting.settlement.dto.BetDto;
import com.sportsbetting.settlement.model.Bet;
import com.sportsbetting.settlement.model.BetStatus;
import com.sportsbetting.settlement.repository.BetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetService {

    private final BetRepository betRepository;

    @Transactional
    public BetDto placeBet(BetDto request) {
        Bet bet = Bet.builder()
                .userId(request.getUserId())
                .eventId(request.getEventId())
                .eventMarketId(request.getEventMarketId())
                .eventWinnerId(request.getEventWinnerId())
                .betAmount(request.getBetAmount())
                .status(BetStatus.OPEN)
                .build();

        Bet saved = betRepository.save(bet);
        log.info("Placed bet: betId={}, userId={}, eventId={}, market={}, selection={}",
                saved.getBetId(), saved.getUserId(), saved.getEventId(),
                saved.getEventMarketId(), saved.getEventWinnerId());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public BetDto getBetById(Long betId) {
        return toDto(betRepository.findById(betId)
                .orElseThrow(() -> new EntityNotFoundException("Bet not found: " + betId)));
    }

    @Transactional(readOnly = true)
    public List<BetDto> getAllBets() {
        return betRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BetDto> getBetsByUser(String userId) {
        return betRepository.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private BetDto toDto(Bet bet) {
        return BetDto.builder()
                .betId(bet.getBetId())
                .userId(bet.getUserId())
                .eventId(bet.getEventId())
                .eventMarketId(bet.getEventMarketId())
                .eventWinnerId(bet.getEventWinnerId())
                .betAmount(bet.getBetAmount())
                .status(bet.getStatus())
                .build();
    }
}
