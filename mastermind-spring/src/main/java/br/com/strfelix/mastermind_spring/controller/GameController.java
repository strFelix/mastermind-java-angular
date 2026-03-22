package br.com.strfelix.mastermind_spring.controller;

import br.com.strfelix.mastermind_spring.dto.request.GuessRequest;
import br.com.strfelix.mastermind_spring.dto.response.GameResponse;
import br.com.strfelix.mastermind_spring.dto.response.GuessResponse;
import br.com.strfelix.mastermind_spring.model.User;
import br.com.strfelix.mastermind_spring.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameResponse> startGame(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.status(201).body(gameService.startGame(token));
    }

    @PostMapping("/{id}/guess")
    public ResponseEntity<GuessResponse> makeGuess(@PathVariable Long id, @Valid @RequestBody GuessRequest request) {
        return ResponseEntity.ok(gameService.makeGuess(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<GameResponse>> getUserHistory(@AuthenticationPrincipal Jwt token){
        return ResponseEntity.ok(gameService.getUserHistory(token));
    }
}
