package com.example.programmers.dto.request;

import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProposalRequest(

        @NotNull(message = "offerType is required")
        OfferType offerType,

        List<BenefitType> benefits,

        @NotNull(message = "customer is required")
        Customer customer

) {}