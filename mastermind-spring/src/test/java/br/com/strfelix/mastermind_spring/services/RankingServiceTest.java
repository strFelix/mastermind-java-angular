package br.com.strfelix.mastermind_spring.services;

import br.com.strfelix.mastermind_spring.dto.response.RankingResponse;
import br.com.strfelix.mastermind_spring.model.User;
import br.com.strfelix.mastermind_spring.repository.UserRepository;
import br.com.strfelix.mastermind_spring.service.RankingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private RankingService rankingService;

    @Test
    void getRanking_shouldReturnUsersOrderedByBestScore() {
        List<User> users = List.of(
                new User(1L, "felix", "felix@email.com", "hash", 1000),
                new User(2L, "admin", "admin@email.com", "hash", 500)
        );

        when(userRepository.findTop10ByOrderByBestScoreDesc()).thenReturn(users);

        List<RankingResponse> ranking = rankingService.getRanking();

        assertEquals(2, ranking.size());
        assertEquals("felix", ranking.get(0).username());
        assertEquals(1000, ranking.get(0).bestScore());
        assertEquals("admin", ranking.get(1).username());
    }

    @Test
    void getRanking_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findTop10ByOrderByBestScoreDesc()).thenReturn(List.of());

        List<RankingResponse> ranking = rankingService.getRanking();

        assertTrue(ranking.isEmpty());
    }
}