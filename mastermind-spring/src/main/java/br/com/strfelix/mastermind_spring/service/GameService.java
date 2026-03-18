package br.com.strfelix.mastermind_spring.service;


import br.com.strfelix.mastermind_spring.dto.response.GameResponse;
import br.com.strfelix.mastermind_spring.dto.request.GuessRequest;
import br.com.strfelix.mastermind_spring.dto.response.GuessResponse;
import br.com.strfelix.mastermind_spring.exceptions.game.GameAlreadyFinishedException;
import br.com.strfelix.mastermind_spring.exceptions.game.GameNotFoundException;
import br.com.strfelix.mastermind_spring.exceptions.user.UserNotFoundException;
import br.com.strfelix.mastermind_spring.model.Game;
import br.com.strfelix.mastermind_spring.model.Guess;
import br.com.strfelix.mastermind_spring.model.User;
import br.com.strfelix.mastermind_spring.repository.GameRepository;
import br.com.strfelix.mastermind_spring.repository.GuessRepository;
import br.com.strfelix.mastermind_spring.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class GameService {

    private static final char[] CHARS = {'A', 'B', 'C', 'D'};
    private static final int CODE_LENGTH = 4;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GuessRepository guessRepository;

    public GameService(GameRepository gameRepository, UserRepository userRepository, GuessRepository guessRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.guessRepository = guessRepository;
    }

    public String generateSecretCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARS.length);
            code.append(CHARS[index]);
        }

        return code.toString();
    }

    private int calculateScore(Game game) {
        return Math.max(0, 100 - (game.getAttempts() * 10));
    }

    private void validateGameNotFinished(Game game) {
        if (game.getEndTime() != null) {
            throw new GameAlreadyFinishedException("Game already finished");
        }
    }

    private int calculateCorrectPositions(String secret, String guess) {
        int correct = 0;
        for (int i = 0; i < secret.length(); i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                correct++;
            }
        }
        return correct;
    }

    private void finalizeGame(Game game, boolean won) {
        if (won) {
            game.setScore(calculateScore(game));
            game.setEndTime(LocalDateTime.now());
        } else if (game.getAttempts() >= 10) {
            game.setScore(0);
            game.setEndTime(LocalDateTime.now());
        }
        User user = game.getUser();
        if (user.getBestScore() < game.getScore()) {
            user.setBestScore(game.getScore());
            userRepository.save(user);
        }
    }

    public Game startGame(Jwt token){
        Long userId = token.getClaim("id");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Game game = new Game(null, generateSecretCode(), 0, 0, LocalDateTime.now(), null, user);

        return gameRepository.save(game);
    }

    public GuessResponse makeGuess(Long gameId, GuessRequest request) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found"));

        validateGameNotFinished(game);

        String userGuess = request.guess().toUpperCase();
        int correctPositions = calculateCorrectPositions(game.getSecretCode(), userGuess);

        game.setAttempts(game.getAttempts() + 1);

        Guess guess = new Guess(null, game.getAttempts(), userGuess, correctPositions, game);
        guessRepository.save(guess);

        boolean won = correctPositions == game.getSecretCode().length();
        finalizeGame(game, won);

        gameRepository.save(game);

        return new GuessResponse(correctPositions, game.getEndTime() != null, won);
    }


    public GameResponse getGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found"));

        boolean finished = game.getEndTime() != null;
        boolean won = finished && game.getScore() != null && game.getScore() > 0;

        return new GameResponse(
                game.getId(),
                game.getAttempts(),
                game.getScore(),
                game.getStartTime(),
                game.getEndTime(),
                finished,
                won
        );
    }
}
