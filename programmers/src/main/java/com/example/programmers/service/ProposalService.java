package com.example.programmers.service;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.CheckingAccount;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import com.example.programmers.repository.ProposalRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProposalService {

    private final ProposalRepository repository;

    public ProposalService(ProposalRepository repository){
        this.repository = repository;
    }



    public  Boolean validateProposal(ProposalDomain proposalDomain){
        CheckingAccount checkingAccount = proposalDomain.getCustomer().checkingAccount();
        BigDecimal monthlyIncome = checkingAccount.monthlyIncome();
        List<BenefitType> benefits = proposalDomain.getBenefits();
        OfferType offer = proposalDomain.getOfferType();
        if (offer.equals(OfferType.A) &&
                !isHigherThan(monthlyIncome,new BigDecimal(1000)))
            return false;

        BigDecimal investmentAmount = checkingAccount.investmentAmount();
        if (offer.equals(OfferType.B) &&
                !isHigherThan(monthlyIncome,new BigDecimal(15000))  &&
                !isHigherThan(investmentAmount, new BigDecimal(5000))
        )
            return  false;

        LocalDateTime currentAccountOpeningDate= checkingAccount.createdAt();
        if (offer.equals(OfferType.B) &&
                !isHigherThan(monthlyIncome, new BigDecimal(50000)) &&
                hasCheckingAccountMoreThanYears(currentAccountOpeningDate,2)
                )
            return false;


        return validateBenefits(benefits, offer);
    }

    private Boolean isHigherThan(BigDecimal monthlyIncome, BigDecimal minimumIncome ){
        return monthlyIncome.compareTo(minimumIncome) > 0;
    }

    private Boolean validateBenefits(List<BenefitType> benefits, OfferType offerType) {
        // Cashback e Pontos são mutuamente exclusivos
        if (benefits.contains(BenefitType.CASHBACK)
                && benefits.contains(BenefitType.POINTS)) {
            return false;
        }
        // Seguro viagem apenas na oferta C
        if (benefits.contains(BenefitType.TRAVEL_INSURANCE)
                && offerType != OfferType.C) {
            return false;
        }

        // Sala VIP apenas nas ofertas B e C
        if (benefits.contains(BenefitType.VIP_LOUNGE)
                && offerType != OfferType.B
                && offerType != OfferType.C) {
            return false;
        }

        return true;
    }
    private Boolean hasCheckingAccountMoreThanYears(
            LocalDateTime openingDate,
            int years) {

        return openingDate.isBefore(LocalDateTime.now().minusYears(years));
    }

}
