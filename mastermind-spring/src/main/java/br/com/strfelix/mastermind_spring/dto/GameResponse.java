package br.com.strfelix.mastermind_spring.dto;

import java.time.LocalDateTime;

public record GameResponse(
        Long id,
        Integer attempts,
        Integer score,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean finished,
        boolean won
) {}
