package com.example.programmers.service;

import com.example.programmers.domain.CardDomain;
import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.Customer;
import com.example.programmers.dto.response.CardResponse;
import com.example.programmers.entity.CardEntity;
import com.example.programmers.mapper.CardMapper;
import com.example.programmers.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository repository;
    private final CardMapper mapper;
    private final EncryptionService encryptionService;

    public CardResponse save(ProposalDomain proposalDomain){
        Customer customer = proposalDomain.getCustomer();
        CardDomain cardDomain = CardDomain.create(customer.name(),
                customer.customerIdentification(),
                proposalDomain.getBenefits());
        String cardNumber = cardDomain.getCardNumber();
        CardEntity cardEntity = mapper.toEntity(
                cardDomain, encryptionService.encrypt(cardNumber)
        );
        this.repository.save(cardEntity);

        return mapper.toResponse(cardEntity);
    }


    public CardResponse findById(UUID id) {

        CardEntity card = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Cartao não encontrado")
                );

        return mapper.toResponse(card);
    }


    public List<CardResponse> findByCustomer(UUID customerIdentification) {

        List<CardEntity> cards = repository
                .findByCustomerIdentification(customerIdentification);

        return cards.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
