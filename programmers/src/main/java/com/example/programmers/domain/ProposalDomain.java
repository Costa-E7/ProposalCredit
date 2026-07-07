package com.example.programmers.domain;

import com.example.programmers.dto.request.CheckingAccount;
import com.example.programmers.dto.request.Customer;
import com.example.programmers.dto.request.ProposalAnalysis;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import com.example.programmers.enums.ProposalStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ProposalDomain {

    private UUID proposalIdentification;
    private OfferType offerType;
    private List<BenefitType> benefits;
    private Customer customer;
    private ProposalAnalysis proposalAnalysis;

    public void setProposalAnalysis(ProposalAnalysis proposalAnalysis) {
        this.proposalAnalysis = proposalAnalysis;
    }


}