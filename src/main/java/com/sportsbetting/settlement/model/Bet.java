package com.sportsbetting.settlement.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Core domain entity. Represents a single bet placed by a user on a specific
 * market outcome within an event.
 */
@Entity
@Table(name = "bets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long betId;

    /** Platform user who placed the bet. */
    @Column(nullable = false)
    private String userId;

    /** The event this bet belongs to. */
    @Column(nullable = false)
    private String eventId;

    /** The specific market within the event (e.g. "match-winner", "first-scorer"). */
    @Column(nullable = false)
    private String eventMarketId;

    /**
     * The outcome the bettor selected as the winner
     * (e.g. a team ID, player ID, or outcome code).
     * Compared against {@code EventOutcome#eventWinnerId} at settlement time.
     */
    @Column(nullable = false)
    private String eventWinnerId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal betAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetStatus status;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = BetStatus.OPEN;
        }
    }
}
