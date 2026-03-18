package br.com.strfelix.mastermind_spring.repository;

import br.com.strfelix.mastermind_spring.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
