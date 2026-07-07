package com.example.programmers.mapper;

import com.example.programmers.domain.CardDomain;
import com.example.programmers.dto.response.CardResponse;
import com.example.programmers.entity.CardEntity;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardResponse toResponse(CardEntity entity) {
        return new CardResponse(
                entity.getId(),
                entity.getLastFourDigits(),
                entity.getExpirationDate(),
                entity.getOwnerName(),
                entity.getCreatedAt(),
                entity.isCashback(),
                entity.isPontos(),
                entity.isSeguro_viagem(),
                entity.isSala_vip()
        );
    }

    public CardEntity toEntity(CardDomain domain, String encryptedCardNumber) {
        return CardEntity.builder()
                .encryptedCardNumber(domain.getCardNumber())
                .lastFourDigits(domain.getLastFourDigits())
                .customerIdentification(domain.getCustomerIdentification())
                .expirationDate(domain.getExpirationDate())
                .ownerName(domain.getOwnerName())
                .createdAt(domain.getCreatedAt())
                .cashback(domain.isCashback())
                .pontos(domain.isPontos())
                .sala_vip(domain.isSala_vip())
                .seguro_viagem(domain.isSeguro_viagem())
                .build();
    }

}