package br.com.strfelix.mastermind_spring.repository;

import br.com.strfelix.mastermind_spring.model.Guess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuessRepository extends JpaRepository<Guess, Long> {
}
