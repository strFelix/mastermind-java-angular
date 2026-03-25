package br.com.strfelix.mastermind_spring.controllers;

import br.com.strfelix.mastermind_spring.controller.AuthController;
import br.com.strfelix.mastermind_spring.dto.request.LoginRequest;
import br.com.strfelix.mastermind_spring.dto.request.RegisterRequest;
import br.com.strfelix.mastermind_spring.dto.response.AuthResponse;
import br.com.strfelix.mastermind_spring.dto.response.UserResponse;
import br.com.strfelix.mastermind_spring.exceptions.auth.InvalidCredentialsException;
import br.com.strfelix.mastermind_spring.exceptions.user.UserAlreadyExistsException;
import br.com.strfelix.mastermind_spring.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean  private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void login_shouldReturn200_whenCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest("felix", "123456");
        AuthResponse response = new AuthResponse("jwt-token");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        LoginRequest request = new LoginRequest("felix", "wrongpassword");

        when(authService.login(any())).thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_shouldReturn201_whenDataIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest("felix", "felix@email.com", "123456");
        UserResponse response = new UserResponse(1L, "felix", "felix@email.com", 0);

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("felix"));
    }

    @Test
    void register_shouldReturn409_whenUserAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("felix", "felix@email.com", "123456");

        when(authService.register(any())).thenThrow(new UserAlreadyExistsException("User already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    void register_shouldReturn400_whenFieldsAreBlank() throws Exception {
        RegisterRequest request = new RegisterRequest("", "", "");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}