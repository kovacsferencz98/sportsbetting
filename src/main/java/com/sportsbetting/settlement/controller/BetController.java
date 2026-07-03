package com.sportsbetting.settlement.controller;

import com.sportsbetting.settlement.dto.BetDto;
import com.sportsbetting.settlement.service.BetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    @PostMapping
    public ResponseEntity<BetDto> placeBet(@RequestBody BetDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(betService.placeBet(request));
    }

    @GetMapping
    public ResponseEntity<List<BetDto>> getAllBets() {
        return ResponseEntity.ok(betService.getAllBets());
    }

    @GetMapping("/{betId}")
    public ResponseEntity<BetDto> getBet(@PathVariable("betId") Long betId) {
        return ResponseEntity.ok(betService.getBetById(betId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BetDto>> getBetsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(betService.getBetsByUser(userId));
    }
}
