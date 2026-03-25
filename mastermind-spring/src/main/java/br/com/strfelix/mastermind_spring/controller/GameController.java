package br.com.strfelix.mastermind_spring.controller;

import br.com.strfelix.mastermind_spring.dto.request.GuessRequest;
import br.com.strfelix.mastermind_spring.dto.response.ErrorResponse;
import br.com.strfelix.mastermind_spring.dto.response.GameResponse;
import br.com.strfelix.mastermind_spring.dto.response.GuessResponse;
import br.com.strfelix.mastermind_spring.dto.response.ValidationErrorResponse;
import br.com.strfelix.mastermind_spring.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@Tag(name = "Game", description = "Mastermind game endpoints")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "Start game", description = "Creates a new game for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Game created successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<GameResponse> startGame(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.status(201).body(gameService.startGame(token));
    }

    @Operation(summary = "Make guess", description = "Submits a 4-letter guess (A-D)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guess processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Game already finished",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/guess")
    public ResponseEntity<GuessResponse> makeGuess(@PathVariable Long id, @Valid @RequestBody GuessRequest request) {
        return ResponseEntity.ok(gameService.makeGuess(id, request));
    }

    @Operation(summary = "Get game", description = "Returns a game by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Game found"),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @Operation(summary = "Game history", description = "Returns the last 5 games of the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "History returned successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/history")
    public ResponseEntity<List<GameResponse>> getUserHistory(@AuthenticationPrincipal Jwt token){
        return ResponseEntity.ok(gameService.getUserHistory(token));
    }
}
