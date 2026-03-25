package br.com.strfelix.mastermind_spring.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ValidationErrorResponse(
        String message,
        List<Map<String, String>> errors,
        LocalDateTime timestamp
) {}
