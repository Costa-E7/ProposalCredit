package com.example.programmers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CheckingAccount(
        @NotBlank(message = "checkingIdentification is required")
        String checkingIdentification,

        @NotNull(message = "createdAt is required")
        LocalDateTime createdAt,

        @NotNull(message = "monthlyIncome is required")
        @Positive(message = "monthlyIncome must be greater than zero")
        BigDecimal monthlyIncome,

        @NotNull(message = "investmentAmount is required")
        @Positive(message = "investmentAmount must be greater than zero")
        BigDecimal investmentAmount
) {}