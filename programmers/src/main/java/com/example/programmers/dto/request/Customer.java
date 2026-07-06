package com.example.programmers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Customer(
        @NotBlank(message = "customerIdentification is required")
        String customerIdentification,

        @NotBlank(message = "name is required")
        String name,

        @NotNull(message = "checkingAccount is required")
        CheckingAccount checkingAccount
) {}
