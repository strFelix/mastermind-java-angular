package br.com.strfelix.mastermind_spring.repository;

import br.com.strfelix.mastermind_spring.model.Game;
import br.com.strfelix.mastermind_spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    public List<Game> findByUserIdOrderByEndTimeDesc(Long userId);

    Long user(User user);
}
