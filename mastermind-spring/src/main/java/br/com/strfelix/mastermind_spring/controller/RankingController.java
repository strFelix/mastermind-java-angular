package br.com.strfelix.mastermind_spring.controller;

import br.com.strfelix.mastermind_spring.dto.response.ErrorResponse;
import br.com.strfelix.mastermind_spring.dto.response.RankingResponse;
import br.com.strfelix.mastermind_spring.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
@Tag(name = "Ranking", description = "Ranking endpoints")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Operation(summary = "Global ranking", description = "Returns the top 10 players ordered by best score")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking returned successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<RankingResponse>> getRanking(){
        return ResponseEntity.ok((rankingService.getRanking()));
    }
}
