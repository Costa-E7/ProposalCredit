package com.example.programmers.mapper;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.CheckingAccount;
import com.example.programmers.dto.request.Customer;
import com.example.programmers.dto.request.ProposalAnalysis;
import com.example.programmers.dto.request.ProposalRequest;
import com.example.programmers.dto.response.ProposalResponse;
import com.example.programmers.entity.ProposalEntity;
import com.example.programmers.enums.ProposalStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProposalMapper {

    public static ProposalDomain toDomain(ProposalRequest request) {

        Customer customer = new Customer(
                request.customer().customerIdentification(),
                request.customer().name(),
                new CheckingAccount(
                        request.customer().checkingAccount().checkingIdentification(),
                        request.customer().checkingAccount().createdAt(),
                        request.customer().checkingAccount().monthlyIncome(),
                        request.customer().checkingAccount().investmentAmount()
                )
        );

        return ProposalDomain.builder()
                .offerType(request.offerType())
                .benefits(request.benefits())
                .customer(customer)
                .build();
    }

    public static ProposalEntity toEntity(ProposalDomain domain) {
        ProposalAnalysis proposalAnalysis = domain.getProposalAnalysis();
        ProposalStatus status = proposalAnalysis.valid();
        return ProposalEntity.builder()
                .customerIdentification(
                        domain.getCustomer().customerIdentification()
                )
                .offerType(
                        domain.getOfferType()
                )
                .status(
                        status
                )
                .benefits(
                        status.equals(ProposalStatus.APPROVED)
                                ? domain.getBenefits()
                                : List.of()
                )
                .rejectionReason(
                        status.equals(ProposalStatus.APPROVED)
                                ? null
                                : proposalAnalysis.reason()
                )
                .createdAt(
                        LocalDateTime.now()
                )
                .build();
    }

    public ProposalResponse toResponseFromEntity(ProposalEntity entity){
        ProposalStatus status = entity.getStatus();
        return ProposalResponse.builder()
                .proposalId(entity.getId())
                .status(status)
                .customerIdentification(entity.getCustomerIdentification())
                .offerType(entity.getOfferType())
                .approvedBenefits(
                status.equals(ProposalStatus.APPROVED)
                        ? entity.getBenefits()
                        : List.of()
                )
                .rejectionReason(
                        status.equals(ProposalStatus.APPROVED)
                        ? null
                        : entity.getRejectionReason()
                )
                .createdAt(entity.getCreatedAt())
                .build();
    }
    public ProposalResponse toResponseFromEntityAndDomain(
            ProposalDomain domain,
            ProposalEntity entity
    ) {
        ProposalStatus status = domain.getProposalAnalysis().valid();
        return ProposalResponse.builder()
                .proposalId(entity.getId())
                .status(status)
                .customerIdentification(
                        domain.getCustomer().customerIdentification()
                )
                .offerType(domain.getOfferType())
                .approvedBenefits(
                        status.equals(ProposalStatus.APPROVED)
                                ? domain.getBenefits()
                                : List.of()
                )
                .rejectionReason(
                        status.equals(ProposalStatus.APPROVED)
                                ? null
                                : domain.getProposalAnalysis().reason()
                )
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
