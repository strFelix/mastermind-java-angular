package br.com.strfelix.mastermind_spring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    private String secretCode;

    private Integer attempts;

    private Integer score;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @ManyToOne
    private User user;

}