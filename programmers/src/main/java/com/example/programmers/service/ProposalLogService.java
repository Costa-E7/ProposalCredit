package com.example.programmers.service;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.entity.ProposalEntity;
import com.example.programmers.entity.ProposalLogEntity;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.ProposalAction;
import com.example.programmers.repository.ProposalLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ProposalLogService {

    private final ObjectMapper objectMapper;
    private final ProposalLogRepository repository;
    private static final Logger log = LoggerFactory.getLogger(ProposalLogService.class);


    public void createProposalLog(ProposalEntity proposalSaved, ProposalDomain proposalDomain, ProposalAction action) {
        try {
            String payload = objectMapper.writeValueAsString(proposalDomain);
            ProposalLogEntity log = ProposalLogEntity.builder()
                    .proposal(proposalSaved)
                    .requestPayload(payload)
                    .createdAt(LocalDateTime.now())
                    .action(action)
                    .build();
            repository.save(log);
        } catch (Exception e) {
            log.error("Erro ao criar propositalLogEntity da proposta {}.",
                    proposalSaved.getId(),
                    e);
            throw new RuntimeException("Erro ao gerar propositalLogEntity da proposta", e);
        }
    }

    public void save(ProposalLogEntity log) {
        repository.save(log);
    }

    public ProposalLogEntity retrieveLastProposal(UUID id) {
       return  repository
                .findFirstByProposalIdOrderByCreatedAtDesc(id)
                .orElseThrow(() -> new RuntimeException("Log da proposta não encontrado"));

    }

    public void createBenefitsUpdateLog(
            ProposalEntity proposal,
            List<BenefitType> benefits
    ) {
        try {
            String payload = objectMapper.writeValueAsString(benefits);

            ProposalLogEntity log = ProposalLogEntity.builder()
                    .id(proposal.getId())
                    .action(ProposalAction.UPDATED)
                    .requestPayload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            repository.save(log);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
