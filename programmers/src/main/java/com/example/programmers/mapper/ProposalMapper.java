package com.example.programmers.mapper;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.CheckingAccount;
import com.example.programmers.dto.request.Customer;
import com.example.programmers.dto.request.ProposalRequest;

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

        return  ProposalDomain.builder()
                .proposalIdentification(request.proposalIdentification())
                .offerType(request.offerType())
                .benefits(request.benefits())
                .customer(customer)
                .build();
    }
}