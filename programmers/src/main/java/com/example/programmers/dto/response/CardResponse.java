package com.example.programmers.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CardResponse(
        UUID id,
        String lastFourDigits,
        LocalDate expirationDate,
        String ownerName,
        LocalDateTime createdAt
) {
}