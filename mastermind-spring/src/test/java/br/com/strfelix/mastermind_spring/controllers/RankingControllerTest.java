package br.com.strfelix.mastermind_spring.controllers;

import br.com.strfelix.mastermind_spring.config.security.SecurityConfig;
import br.com.strfelix.mastermind_spring.controller.RankingController;
import br.com.strfelix.mastermind_spring.dto.response.RankingResponse;
import br.com.strfelix.mastermind_spring.service.RankingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RankingController.class)
@Import(SecurityConfig.class)
class RankingControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean
    private RankingService rankingService;
    @MockitoBean  private JwtDecoder jwtDecoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getRanking_shouldReturn200_whenAuthenticated() throws Exception {
        List<RankingResponse> ranking = List.of(
                new RankingResponse("felix", 1000),
                new RankingResponse("admin", 500)
        );

        when(rankingService.getRanking()).thenReturn(ranking);

        mockMvc.perform(get("/ranking")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("felix"))
                .andExpect(jsonPath("$[0].bestScore").value(1000));
    }

    @Test
    void getRanking_shouldReturn200_whenEmpty() throws Exception {
        when(rankingService.getRanking()).thenReturn(List.of());

        mockMvc.perform(get("/ranking")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRanking_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/ranking"))
                .andExpect(status().isUnauthorized());
    }
}