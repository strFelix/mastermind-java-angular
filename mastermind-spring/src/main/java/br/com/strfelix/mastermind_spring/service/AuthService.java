package br.com.strfelix.mastermind_spring.service;

import br.com.strfelix.mastermind_spring.dto.request.LoginRequest;
import br.com.strfelix.mastermind_spring.dto.request.RegisterRequest;
import br.com.strfelix.mastermind_spring.dto.response.AuthResponse;
import br.com.strfelix.mastermind_spring.dto.response.UserResponse;
import br.com.strfelix.mastermind_spring.exceptions.auth.InvalidCredentialsException;
import br.com.strfelix.mastermind_spring.exceptions.user.UserAlreadyExistsException;
import br.com.strfelix.mastermind_spring.model.User;
import br.com.strfelix.mastermind_spring.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {

        userRepository.findByUsername(request.username())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User already exists");
                });

        String hashed = passwordEncoder.encode(request.password());
        User user = new User(null, request.username(), request.email(), hashed, 0);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token
        );
    }
}
