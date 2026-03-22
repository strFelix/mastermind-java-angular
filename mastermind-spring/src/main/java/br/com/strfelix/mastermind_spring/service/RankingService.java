package br.com.strfelix.mastermind_spring.service;

import br.com.strfelix.mastermind_spring.dto.response.RankingResponse;
import br.com.strfelix.mastermind_spring.model.User;
import br.com.strfelix.mastermind_spring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {

    private final UserRepository userRepository;

    public RankingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<RankingResponse> getRanking() {
        return userRepository.findTop10ByOrderByBestScoreDesc()
                .stream()
                .map(user -> new RankingResponse(
                        user.getUsername(),
                        user.getBestScore()
                ))
                .toList();
    }

}
