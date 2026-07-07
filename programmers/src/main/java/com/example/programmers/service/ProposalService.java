package com.example.programmers.service;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.CheckingAccount;
import com.example.programmers.dto.request.ProposalAnalysis;
import com.example.programmers.dto.response.ProposalResponse;
import com.example.programmers.entity.ProposalEntity;
import com.example.programmers.entity.ProposalLogEntity;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import com.example.programmers.enums.ProposalAction;
import com.example.programmers.enums.ProposalStatus;
import com.example.programmers.mapper.ProposalMapper;
import com.example.programmers.repository.ProposalLogRepository;
import com.example.programmers.repository.ProposalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.validator.cfg.defs.UUIDDef;
import org.springframework.boot.actuate.cache.NonUniqueCacheException;
import org.springframework.stereotype.Service;

import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class ProposalService {

    private final ProposalRepository repository;

    private final ProposalLogRepository proposalLogRepository;

    private final ProposalMapper mapper;

    private CardService cardService;

    private ObjectMapper objectMapper;


    @Transactional
    public ProposalResponse createProposal(ProposalDomain proposalDomain) {
        ProposalAnalysis proposalAnalysis = isProposalValid(proposalDomain);
        proposalDomain.setProposalAnalysis(proposalAnalysis);
        ProposalEntity entity = mapper.toEntity(proposalDomain);
        ProposalEntity proposalSaved = this.repository.save(entity);
        cardService.save(proposalDomain);
        createProposalLog(proposalSaved, proposalDomain);
        //criar conta cartao, e add alguma ferramenta de log
        return mapper.toResponseFromEntityAndDomain(proposalDomain, proposalSaved);
    }

    public ProposalResponse findOne(UUID id) {
        ProposalEntity proposal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nao encontramos essa proposta"));
        return mapper.toResponseFromEntity(proposal);
    }


    public List<ProposalResponse> findAllByUser(UUID customerIdentification) {
        List<ProposalEntity> proposals =
                repository.findAllByCustomerIdentification(customerIdentification);
        return proposals.stream()
                .map(mapper::toResponseFromEntity)
                .toList();
    }

    public List<ProposalResponse> findAllByStatus(
            ProposalStatus status
    ) {
        return repository.findAllByStatus(status)
                .stream()
                .map(mapper::toResponseFromEntity)
                .toList();
    }


    public List<ProposalResponse> findAllByUserAndStatus(
            UUID customerIdentification,
            ProposalStatus status
    ) {

        return repository
                .findAllByCustomerIdentificationAndStatus(
                        customerIdentification,
                        status
                )
                .stream()
                .map(mapper::toResponseFromEntity)
                .toList();
    }

    public ProposalResponse updateBenefits(
            UUID id,
            List<BenefitType> benefits
    ) {
        ProposalEntity proposal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nao encontramos essa proposta"));
        if (proposal.getStatus().equals(ProposalStatus.REJECTED)) {
            throw new IllegalStateException(
                    "Nao e possível atualizar os beneficios " +
                            "porque o cliente não atende aos requisitos mínimos desta oferta"
            );
        }
        validateBenefits(benefits, proposal.getOfferType());
        ProposalAnalysis proposalAnalysis = validateBenefits(benefits, proposal.getOfferType());
        ProposalStatus status = proposalAnalysis.valid();
        if (status.equals(ProposalStatus.APPROVED)) {
            proposal.setBenefits(benefits);
            proposal.setStatus(status);
            proposal.setRejectionReason("");
        }
        if (status.equals(ProposalStatus.REJECTED_BY_BENEFIT)) {
            proposal.setRejectionReason(proposalAnalysis.reason());
            proposal.setStatus(ProposalStatus.REJECTED_BY_BENEFIT);
        }
        proposal.setUpdatedAt();
        ProposalEntity saved = repository.save(proposal);
        createBenefitsUpdateLog(saved, benefits);

        return null;
    }

    //Regras de negocio core
    public ProposalAnalysis isProposalValid(ProposalDomain proposalDomain) {
        CheckingAccount checkingAccount = proposalDomain.getCustomer().checkingAccount();
        BigDecimal monthlyIncome = checkingAccount.monthlyIncome();
        List<BenefitType> benefits = proposalDomain.getBenefits();
        OfferType offer = proposalDomain.getOfferType();
        if (offer.equals(OfferType.A) &&
                !isHigherThan(monthlyIncome, new BigDecimal(1000)))
            return new ProposalAnalysis(ProposalStatus.REJECTED, "Oferta A exige renda superior a R$ 1.000.");

        BigDecimal investmentAmount = checkingAccount.investmentAmount();
        if (offer.equals(OfferType.B) &&
                !isHigherThan(monthlyIncome, new BigDecimal(15000)) &&
                !isHigherThan(investmentAmount, new BigDecimal(5000))
        )
            return new ProposalAnalysis(ProposalStatus.REJECTED,
                    "\"Oferta B exige renda superior a R$ 15.000 ou investimento superior a R$ 5.000.\"\n");

        LocalDateTime currentAccountOpeningDate = checkingAccount.createdAt();
        if (offer.equals(OfferType.C) &&
                !isHigherThan(monthlyIncome, new BigDecimal(50000)) &&
                hasCheckingAccountMoreThanYears(currentAccountOpeningDate, 2)
        )
            return new ProposalAnalysis(
                    ProposalStatus.REJECTED,
                    "Oferta C exige renda superior a R$ 50.000 e conta com mais de 2 anos."
            );


        return validateBenefits(benefits, offer);
    }

    private boolean isHigherThan(BigDecimal monthlyIncome, BigDecimal minimumIncome) {
        return monthlyIncome.compareTo(minimumIncome) > 0;
    }

    private ProposalAnalysis validateBenefits(List<BenefitType> benefits, OfferType offerType) {
        // Cashback e Pontos são mutuamente exclusivos
        if (benefits.contains(BenefitType.CASHBACK)
                && benefits.contains(BenefitType.PONTOS)) {
            return new ProposalAnalysis(ProposalStatus.REJECTED_BY_BENEFIT,
                    " \"Cashback e Pontos não podem ser escolhidos juntos.\"");
        }
        // Seguro viagem apenas na oferta C
        if (benefits.contains(BenefitType.SEGURO_VIAGEM)
                && offerType != OfferType.C) {
            return new ProposalAnalysis(ProposalStatus.REJECTED_BY_BENEFIT,
                    "Seguro viagem está disponível apenas para a oferta C.");
        }

        // Sala VIP apenas nas ofertas B e C
        if (benefits.contains(BenefitType.SALA_VIP)
                && offerType != OfferType.B
                && offerType != OfferType.C) {
            return new ProposalAnalysis(
                    ProposalStatus.REJECTED_BY_BENEFIT,
                    "Sala VIP está disponível apenas para as ofertas B e C."
            );
        }

        return new ProposalAnalysis(
                ProposalStatus.APPROVED,
                benefits.stream()
                        .map(BenefitType::name)
                        .collect(Collectors.joining(", "))
        );
    }

    private boolean hasCheckingAccountMoreThanYears(
            LocalDateTime openingDate,
            int years) {

        return openingDate.isBefore(LocalDateTime.now().minusYears(years));
    }

    private void createProposalLog(ProposalEntity proposalSaved, ProposalDomain proposalDomain) {
        try {
            String payload = objectMapper.writeValueAsString(proposalDomain);
            ProposalLogEntity log = ProposalLogEntity.builder()
                    .proposal(proposalSaved)
                    .requestPayload(payload)
                    .createdAt(LocalDateTime.now())
                    .action(ProposalAction.CREATED)
                    .build();
            proposalLogRepository.save(log);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar log da proposta", e);
        }
    }

    private void createBenefitsUpdateLog(
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

            proposalLogRepository.save(log);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
