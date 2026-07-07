package com.example.programmers.service;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.CheckingAccount;
import com.example.programmers.dto.request.ProposalAnalysis;
import com.example.programmers.entity.ProposalEntity;
import com.example.programmers.entity.ProposalLogEntity;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import com.example.programmers.enums.ProposalStatus;
import com.example.programmers.mapper.ProposalMapper;
import com.example.programmers.repository.ProposalLogRepository;
import com.example.programmers.repository.ProposalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProposalService {

    private final ProposalRepository repository;

    private final ProposalLogRepository proposalLogRepository;

    private final ProposalMapper mapper;

    private ObjectMapper objectMapper;
    public ProposalService(ProposalRepository repository,
                           ProposalLogRepository proposalLogRepository,
                           ProposalMapper mapper,
                           ObjectMapper objectMapper){
        this.repository = repository;
        this.proposalLogRepository = proposalLogRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    public ProposalAnalysis createProposal(ProposalDomain proposalDomain) {
        ProposalAnalysis proposalAnalysis = isProposalValid(proposalDomain);
        proposalDomain.setProposalAnalysis(proposalAnalysis);
        ProposalEntity entity = mapper.toEntity(proposalDomain);
        ProposalEntity proposalSaved = this.repository.save(entity);
        createProposalLog(proposalSaved, proposalDomain);
        return proposalAnalysis;
//        this.repository.save()
    }


    public ProposalAnalysis isProposalValid(ProposalDomain proposalDomain){
        CheckingAccount checkingAccount = proposalDomain.getCustomer().checkingAccount();
        BigDecimal monthlyIncome = checkingAccount.monthlyIncome();
        List<BenefitType> benefits = proposalDomain.getBenefits();
        OfferType offer = proposalDomain.getOfferType();
        if (offer.equals(OfferType.A) &&
                !isHigherThan(monthlyIncome,new BigDecimal(1000)))
            return new ProposalAnalysis(ProposalStatus.REJECTED, "Oferta A exige renda superior a R$ 1.000.");

        BigDecimal investmentAmount = checkingAccount.investmentAmount();
        if (offer.equals(OfferType.B) &&
                !isHigherThan(monthlyIncome,new BigDecimal(15000))  &&
                !isHigherThan(investmentAmount, new BigDecimal(5000))
        )
            return  new ProposalAnalysis(ProposalStatus.REJECTED,
                    "\"Oferta B exige renda superior a R$ 15.000 ou investimento superior a R$ 5.000.\"\n");

        LocalDateTime currentAccountOpeningDate= checkingAccount.createdAt();
        if (offer.equals(OfferType.C) &&
                !isHigherThan(monthlyIncome, new BigDecimal(50000)) &&
                hasCheckingAccountMoreThanYears(currentAccountOpeningDate,2)
                )
            return new ProposalAnalysis(
                ProposalStatus.REJECTED,
                "Oferta C exige renda superior a R$ 50.000 e conta com mais de 2 anos."
        );


        return validateBenefits(benefits, offer);
    }

    private boolean isHigherThan(BigDecimal monthlyIncome, BigDecimal minimumIncome ){
        return monthlyIncome.compareTo(minimumIncome) > 0;
    }

    private ProposalAnalysis validateBenefits(List<BenefitType> benefits, OfferType offerType) {
        // Cashback e Pontos são mutuamente exclusivos
        if (benefits.contains(BenefitType.CASHBACK)
                && benefits.contains(BenefitType.PONTOS)) {
            return new ProposalAnalysis(ProposalStatus.REJECTED,
                    " \"Cashback e Pontos não podem ser escolhidos juntos.\"");
        }
        // Seguro viagem apenas na oferta C
        if (benefits.contains(BenefitType.SEGURO_VIAGEM)
                && offerType != OfferType.C) {
            return new ProposalAnalysis(ProposalStatus.REJECTED,
                    "Seguro viagem está disponível apenas para a oferta C.");
        }

        // Sala VIP apenas nas ofertas B e C
        if (benefits.contains(BenefitType.SALA_VIP)
                && offerType != OfferType.B
                && offerType != OfferType.C) {
            return new ProposalAnalysis(
                    ProposalStatus.REJECTED,
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
                    .build();
            proposalLogRepository.save(log);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar log da proposta", e);
        }
    }
}
