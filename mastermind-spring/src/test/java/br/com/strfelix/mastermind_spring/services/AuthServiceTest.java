package br.com.strfelix.mastermind_spring.services;

import br.com.strfelix.mastermind_spring.dto.request.LoginRequest;
import br.com.strfelix.mastermind_spring.dto.request.RegisterRequest;
import br.com.strfelix.mastermind_spring.dto.response.AuthResponse;
import br.com.strfelix.mastermind_spring.dto.response.UserResponse;
import br.com.strfelix.mastermind_spring.exceptions.auth.InvalidCredentialsException;
import br.com.strfelix.mastermind_spring.exceptions.user.UserAlreadyExistsException;
import br.com.strfelix.mastermind_spring.model.User;
import br.com.strfelix.mastermind_spring.repository.UserRepository;
import br.com.strfelix.mastermind_spring.service.AuthService;
import br.com.strfelix.mastermind_spring.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "felix", "felix@email.com", "hashed123", 0);
    }

    @Test
    void register_shouldCreateUser_whenUsernameNotExists() {
        RegisterRequest request = new RegisterRequest("felix", "felix@email.com", "123456");

        when(userRepository.findByUsername("felix")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashed123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("felix", response.username());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest("felix", "felix@email.com", "123456");

        when(userRepository.findByUsername("felix")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("felix", "123456");

        when(userRepository.findByUsername("felix")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "hashed123")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        LoginRequest request = new LoginRequest("unknown", "123456");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrow_whenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("felix", "wrongpassword");

        when(userRepository.findByUsername("felix")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashed123")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}