package com.example.programmers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record Customer(
        @NotNull(message = "customerIdentification is required")
        UUID customerIdentification,

        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "checkingAccount is required")
        CheckingAccount checkingAccount
) {}
