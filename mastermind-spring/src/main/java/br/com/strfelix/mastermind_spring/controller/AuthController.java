package br.com.strfelix.mastermind_spring.controller;

import br.com.strfelix.mastermind_spring.dto.request.LoginRequest;
import br.com.strfelix.mastermind_spring.dto.request.RegisterRequest;
import br.com.strfelix.mastermind_spring.dto.response.AuthResponse;
import br.com.strfelix.mastermind_spring.dto.response.UserResponse;
import br.com.strfelix.mastermind_spring.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }
}