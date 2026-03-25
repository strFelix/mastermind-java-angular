package br.com.strfelix.mastermind_spring.exceptions;

import br.com.strfelix.mastermind_spring.dto.response.ErrorResponse;
import br.com.strfelix.mastermind_spring.dto.response.ValidationErrorResponse;
import br.com.strfelix.mastermind_spring.exceptions.auth.InvalidCredentialsException;
import br.com.strfelix.mastermind_spring.exceptions.game.GameAlreadyFinishedException;
import br.com.strfelix.mastermind_spring.exceptions.game.GameNotFoundException;
import br.com.strfelix.mastermind_spring.exceptions.user.UserAlreadyExistsException;
import br.com.strfelix.mastermind_spring.exceptions.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private ErrorResponse error(String message) {
        return new ErrorResponse(message, LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        String message;

        if ("dev".equals(activeProfile)) {
            message = ex.getMessage();
        } else {
            message = "Internal server error";
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error(message));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserExists(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity.badRequest().body(
                new ValidationErrorResponse("Validation error", errors, LocalDateTime.now())
        );
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<?> handleGameNotFound(GameNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error(ex.getMessage()));
    }

    @ExceptionHandler(GameAlreadyFinishedException.class)
    public ResponseEntity<?> handleGameAlreadyFinished(GameAlreadyFinishedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error(ex.getMessage()));
    }
}
