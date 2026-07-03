package com.sportsbetting.settlement.repository;

import com.sportsbetting.settlement.model.Bet;
import com.sportsbetting.settlement.model.BetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

    List<Bet> findByUserId(String userId);

    List<Bet> findByEventIdAndStatus(String eventId, BetStatus status);

    List<Bet> findByEventId(String eventId);
}
