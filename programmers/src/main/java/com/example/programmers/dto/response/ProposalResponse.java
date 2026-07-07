package com.example.programmers.dto.response;

import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import com.example.programmers.enums.ProposalStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ProposalResponse(
        UUID proposalId,
        ProposalStatus status,
        String customerIdentification,
        OfferType offerType,
        List<BenefitType> approvedBenefits,
        String rejectionReason,
        LocalDateTime createdAt
) {}