package com.example.programmers.domain;

import com.example.programmers.dto.request.Customer;
import com.example.programmers.dto.request.ProposalAnalysis;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ProposalDomain {
    @Setter
    private  UUID id;

    private UUID proposalIdentification;

    private OfferType offerType;

    @Setter
    private List<BenefitType> benefits;

    private Customer customer;

    @Setter
    private ProposalAnalysis proposalAnalysis;

}