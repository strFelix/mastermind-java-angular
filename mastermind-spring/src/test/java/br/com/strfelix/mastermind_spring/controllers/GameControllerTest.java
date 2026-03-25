package br.com.strfelix.mastermind_spring.controllers;

import br.com.strfelix.mastermind_spring.config.security.SecurityConfig;
import br.com.strfelix.mastermind_spring.controller.GameController;
import br.com.strfelix.mastermind_spring.dto.request.GuessRequest;
import br.com.strfelix.mastermind_spring.dto.response.GameResponse;
import br.com.strfelix.mastermind_spring.dto.response.GuessResponse;
import br.com.strfelix.mastermind_spring.exceptions.game.GameAlreadyFinishedException;
import br.com.strfelix.mastermind_spring.exceptions.game.GameNotFoundException;
import br.com.strfelix.mastermind_spring.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
@Import(SecurityConfig.class)
class GameControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean
    private GameService gameService;
    @MockitoBean  private JwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private GameResponse gameResponse;

    @BeforeEach
    void setUp() {
        gameResponse = new GameResponse(1L, 0, 0, LocalDateTime.now(), null, false, false);
    }

    @Test
    void startGame_shouldReturn201_whenAuthenticated() throws Exception {
        when(gameService.startGame(any())).thenReturn(gameResponse);

        mockMvc.perform(post("/game")
                        .with(jwt().jwt(j -> j.claim("id", 1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void startGame_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void makeGuess_shouldReturn200_whenGuessIsValid() throws Exception {
        GuessRequest request = new GuessRequest("ABCD");
        GuessResponse response = new GuessResponse(4, true, true, null);

        when(gameService.makeGuess(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/game/1/guess")
                        .with(jwt().jwt(j -> j.claim("id", 1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.won").value(true))
                .andExpect(jsonPath("$.correctPositions").value(4));
    }

    @Test
    void makeGuess_shouldReturn400_whenGuessIsInvalid() throws Exception {
        GuessRequest request = new GuessRequest("AB");

        mockMvc.perform(post("/game/1/guess")
                        .with(jwt().jwt(j -> j.claim("id", 1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void makeGuess_shouldReturn404_whenGameNotFound() throws Exception {
        GuessRequest request = new GuessRequest("ABCD");

        when(gameService.makeGuess(eq(99L), any())).thenThrow(new GameNotFoundException("Game not found"));

        mockMvc.perform(post("/game/99/guess")
                        .with(jwt().jwt(j -> j.claim("id", 1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Game not found"));
    }

    @Test
    void makeGuess_shouldReturn409_whenGameAlreadyFinished() throws Exception {
        GuessRequest request = new GuessRequest("ABCD");

        when(gameService.makeGuess(eq(1L), any())).thenThrow(new GameAlreadyFinishedException("Game already finished"));

        mockMvc.perform(post("/game/1/guess")
                        .with(jwt().jwt(j -> j.claim("id", 1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Game already finished"));
    }

    @Test
    void getGame_shouldReturn200_whenGameExists() throws Exception {
        when(gameService.getGame(1L)).thenReturn(gameResponse);

        mockMvc.perform(get("/game/1")
                        .with(jwt().jwt(j -> j.claim("id", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getGame_shouldReturn404_whenGameNotFound() throws Exception {
        when(gameService.getGame(99L)).thenThrow(new GameNotFoundException("Game not found"));

        mockMvc.perform(get("/game/99")
                        .with(jwt().jwt(j -> j.claim("id", 1L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Game not found"));
    }

    @Test
    void getUserHistory_shouldReturn200_whenAuthenticated() throws Exception {
        when(gameService.getUserHistory(any())).thenReturn(List.of(gameResponse));

        mockMvc.perform(get("/game/history")
                        .with(jwt().jwt(j -> j.claim("id", 1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}