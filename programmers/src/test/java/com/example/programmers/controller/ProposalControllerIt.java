package com.example.programmers.controller;

import com.example.programmers.dto.request.CheckingAccount;
import com.example.programmers.dto.request.Customer;
import com.example.programmers.dto.request.ProposalRequest;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProposalControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateProposal() throws Exception {
        CheckingAccount checkingAccount = new CheckingAccount(
                "123456",
                LocalDateTime.now(),
                BigDecimal.valueOf(8000),
                BigDecimal.valueOf(15000)
        );
        Customer customer = new Customer(
                UUID.randomUUID(),
                "Eduardo Costa",
                checkingAccount
        );
        ProposalRequest request = new ProposalRequest(
                OfferType.C,
                List.of(BenefitType.SEGURO_VIAGEM),
                customer
        );
        mockMvc.perform(post("/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}