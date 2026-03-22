package br.com.strfelix.mastermind_spring.dto.response;

public record RankingResponse(
    String username,
    Integer bestScore
) { }
