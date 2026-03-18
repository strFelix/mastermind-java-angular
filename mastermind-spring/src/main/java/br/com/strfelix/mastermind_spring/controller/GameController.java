package br.com.strfelix.mastermind_spring.controller;

import br.com.strfelix.mastermind_spring.dto.request.GuessRequest;
import br.com.strfelix.mastermind_spring.model.User;
import br.com.strfelix.mastermind_spring.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<?> startGame(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.status(201).body(gameService.startGame(token));
    }

    @PostMapping("{id}/guess")
    public ResponseEntity<?> makeGuess(@PathVariable Long id, @Valid @RequestBody GuessRequest request) {
        return ResponseEntity.ok(gameService.makeGuess(id, request));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getGame(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }
}
