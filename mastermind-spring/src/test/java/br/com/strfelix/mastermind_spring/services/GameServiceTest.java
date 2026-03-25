package br.com.strfelix.mastermind_spring.services;

import br.com.strfelix.mastermind_spring.dto.request.GuessRequest;
import br.com.strfelix.mastermind_spring.dto.response.GameResponse;
import br.com.strfelix.mastermind_spring.dto.response.GuessResponse;
import br.com.strfelix.mastermind_spring.exceptions.game.GameAlreadyFinishedException;
import br.com.strfelix.mastermind_spring.exceptions.game.GameNotFoundException;
import br.com.strfelix.mastermind_spring.exceptions.user.UserNotFoundException;
import br.com.strfelix.mastermind_spring.model.Game;
import br.com.strfelix.mastermind_spring.model.User;
import br.com.strfelix.mastermind_spring.repository.GameRepository;
import br.com.strfelix.mastermind_spring.repository.GuessRepository;
import br.com.strfelix.mastermind_spring.repository.UserRepository;
import br.com.strfelix.mastermind_spring.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock private GameRepository gameRepository;
    @Mock private UserRepository userRepository;
    @Mock private GuessRepository guessRepository;
    @Mock private Jwt jwt;

    @InjectMocks private GameService gameService;

    private User user;
    private Game game;

    @BeforeEach
    void setUp() {
        user = new User(1L, "felix", "felix@email.com", "hashed123", 0);
        game = new Game(1L, "ABCD", 0, 0, LocalDateTime.now(), null, user);
    }

    @Test
    void startGame_shouldCreateGame_whenUserExists() {
        when(jwt.getClaim("id")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        GameResponse response = gameService.startGame(jwt);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void startGame_shouldThrow_whenUserNotFound() {
        when(jwt.getClaim("id")).thenReturn(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> gameService.startGame(jwt));
    }

    @Test
    void makeGuess_shouldReturnCorrectPositions_whenGuessIsPartiallyCorrect() {
        GuessRequest request = new GuessRequest("ABCC");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(guessRepository.save(any())).thenReturn(null);
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        GuessResponse response = gameService.makeGuess(1L, request);

        assertNotNull(response);
        assertEquals(3, response.correctPositions()); // ABCD vs ABCC = 3 corretas
        assertFalse(response.won());
    }

    @Test
    void makeGuess_shouldWin_whenGuessIsCorrect() {
        GuessRequest request = new GuessRequest("ABCD");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(guessRepository.save(any())).thenReturn(null);
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        GuessResponse response = gameService.makeGuess(1L, request);

        assertTrue(response.won());
        assertTrue(response.finished());
        assertNull(response.secretCode());
    }

    @Test
    void makeGuess_shouldRevealSecretCode_whenGameIsLost() {
        game.setAttempts(9);
        GuessRequest request = new GuessRequest("AAAA");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(guessRepository.save(any())).thenReturn(null);
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        GuessResponse response = gameService.makeGuess(1L, request);

        assertTrue(response.finished());
        assertFalse(response.won());
        assertEquals("ABCD", response.secretCode());
    }

    @Test
    void makeGuess_shouldThrow_whenGameAlreadyFinished() {
        game.setEndTime(LocalDateTime.now());
        GuessRequest request = new GuessRequest("ABCD");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        assertThrows(GameAlreadyFinishedException.class, () -> gameService.makeGuess(1L, request));
    }

    @Test
    void makeGuess_shouldThrow_whenGameNotFound() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () -> gameService.makeGuess(99L, new GuessRequest("ABCD")));
    }

    @Test
    void generateSecretCode_shouldReturn4CharCode() {
        String code = gameService.generateSecretCode();

        assertEquals(4, code.length());
        assertTrue(code.matches("[ABCD]+"));
    }
}