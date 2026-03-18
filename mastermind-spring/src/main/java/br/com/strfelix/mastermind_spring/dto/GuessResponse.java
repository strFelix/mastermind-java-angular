package br.com.strfelix.mastermind_spring.dto;

public record GuessResponse(
        Integer correctPositions,
        boolean finished,
        boolean won
) {}