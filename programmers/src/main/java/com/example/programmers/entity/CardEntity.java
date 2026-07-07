package com.example.programmers.entity;

import com.example.programmers.enums.BenefitType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // se eu tivesse a arquitetura do sistema todo, aqui eu referenciaria a table
    // de usuario, onde um usuario teria poderia ter n cartoes, ficaria, provavelmente assim:
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "customer_id",
//            nullable = false
//    )
//    private UserEntity customer;

    @Column(nullable = false)
    private UUID customerIdentification;

    @Column(nullable = false)
    private String encryptedCardNumber;

    @Column(nullable = false, length = 4)
    private String lastFourDigits;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean cashback;

    @Column(nullable = false)
    private boolean pontos;

    @Column(nullable = false)
    private boolean seguro_viagem;

    @Column(nullable = false)
    private boolean sala_vip;
}