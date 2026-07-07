package com.example.programmers.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.smartcardio.Card;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;


@Getter
@Builder
public class CardDomain {

    private UUID id;

    @Setter
    private String cardNumber;

    @Setter
    private String lastFourDigits;

    private LocalDate expirationDate;

    private String ownerName;

    private LocalDateTime createdAt;
    private UUID customerIdentification;


    public static CardDomain create(String ownerName, UUID customerIdentification) {

        CardDomain cardDomain = CardDomain.builder()
                .cardNumber(generateCardNumber())
                .customerIdentification(customerIdentification)
                .expirationDate(generateExpirationDate())
                .ownerName(ownerName)
                .createdAt(LocalDateTime.now())
                .build();

        String cardNumber = cardDomain.getCardNumber();
        cardDomain.setLastFourDigits(cardNumber.substring(cardNumber.length() - 4));
        return cardDomain;
    }


    private static String generateCardNumber() {
        SecureRandom random = new SecureRandom();
        return random.ints(16, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }


    private static LocalDate generateExpirationDate() {
        LocalDate date = LocalDate.now().plusYears(5);
        return date.withDayOfMonth(date.lengthOfMonth());
    }
}