package com.example.programmers.service;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.enums.OfferType;
import com.example.programmers.repository.ProposalRepository;
import org.springframework.stereotype.Service;

@Service
public class ProposalService {

    private final ProposalRepository repository;

    public ProposalService(ProposalRepository repository){
        this.repository = repository;
    }

    public void validateProposal(ProposalDomain proposal){
            if (proposal.getOfferType().equals(OfferType.A)){
                validateProposalA(proposal);
            }
    }

    public  void validateProposalA(ProposalDomain proposalDomain){
//        proposalDomain.
    }
}
