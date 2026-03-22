package br.com.strfelix.mastermind_spring.controller;

import br.com.strfelix.mastermind_spring.dto.response.RankingResponse;
import br.com.strfelix.mastermind_spring.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public ResponseEntity<List<RankingResponse>> getRanking(){
        return ResponseEntity.ok((rankingService.getRanking()));
    }
}
