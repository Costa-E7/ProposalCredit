package com.example.programmers.dto.request;

import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ProposalRequest(
        @NotNull(message = "proposalIdentification is required")
        UUID proposalIdentification,

        @NotNull(message = "offerType is required")
        OfferType offerType,

        List<BenefitType> benefits,

        @NotNull(message = "customer is required")
        Customer customer

) {}