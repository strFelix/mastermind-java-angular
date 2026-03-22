package br.com.strfelix.mastermind_spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Guess {

    @Id
    @GeneratedValue
    private Long id;

    private Integer attemptNumber;

    private String guessValue;

    private Integer correctPositions;

    @ManyToOne
    private Game game;
}
