package br.com.strfelix.mastermind_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuessRequest(

        @NotBlank(message = "Guess is required")
        @Size(min = 4, max = 4, message = "Guess must have 4 characters")
        String guess

) {}
