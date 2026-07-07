package com.example.programmers.service;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.response.ProposalResponse;
import com.example.programmers.entity.ProposalEntity;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import com.example.programmers.enums.ProposalStatus;
import com.example.programmers.exception.InvalidProposalStateException;
import com.example.programmers.mapper.ProposalMapper;
import com.example.programmers.repository.ProposalRepository;
import com.example.programmers.repository.ProposalLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProposalServiceTest {


    @Mock
    private ProposalRepository repository;

    @Mock
    private ProposalMapper mapper;

    @Mock
    private CardService cardService;

    @Mock
    private ProposalLogService proposalLogService;

    @Mock
    private ObjectMapper objectMapper;


    @InjectMocks
    private ProposalService service;


    @Test
    void shouldFindProposalById() {
        UUID id = UUID.randomUUID();
        ProposalEntity entity = new ProposalEntity();
        ProposalResponse response = new ProposalResponse(
                id,
                ProposalStatus.APPROVED,
                UUID.randomUUID(),
                OfferType.A,
                List.of(BenefitType.PONTOS),
                null,
                LocalDateTime.now()
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(entity));

        when(mapper.toResponseFromEntity(entity))
                .thenReturn(response);
        ProposalResponse result =
                service.findOne(id);
        assertThat(result)
                .isEqualTo(response);

        verify(repository)
                .findById(id);

        verify(mapper)
                .toResponseFromEntity(entity);
    }


    @Test
    void shouldThrowExceptionWhenProposalDoesNotExist() {

        UUID id = UUID.randomUUID();
        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.findOne(id)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Nao encontramos essa proposta");

        verify(mapper, never())
                .toResponseFromEntity(any());

    }


    @Test
    void shouldRejectBenefitUpdateWhenProposalIsApproved() {

        UUID id = UUID.randomUUID();

        ProposalEntity proposal = new ProposalEntity();
        proposal.setStatus(ProposalStatus.APPROVED);
        when(repository.findById(id))
                .thenReturn(Optional.of(proposal));
        assertThatThrownBy(() ->
                service.updateBenefits(
                        id,
                        List.of(BenefitType.PONTOS)
                ))
                .isInstanceOf(InvalidProposalStateException.class)
                .hasMessageContaining(
                        "proposta já foi aprovada"
                );


        verify(repository, never())
                .save(any());

    }


    @Test
    void shouldRejectBenefitUpdateWhenProposalWasRejected() {

        UUID id = UUID.randomUUID();
        ProposalEntity proposal = new ProposalEntity();
        proposal.setStatus(
                ProposalStatus.REJECTED
        );
        when(repository.findById(id))
                .thenReturn(Optional.of(proposal));
        assertThatThrownBy(() ->
                service.updateBenefits(
                        id,
                        List.of(BenefitType.PONTOS)
                ))
                .isInstanceOf(InvalidProposalStateException.class);


    }


    @Test
    void shouldReturnAllProposalsByUser() {

        UUID customerId = UUID.randomUUID();
        ProposalEntity entity = new ProposalEntity();
        ProposalResponse response =
                ProposalResponse.builder()
                        .proposalId(UUID.randomUUID())
                        .status(ProposalStatus.APPROVED)
                        .customerIdentification(customerId)
                        .offerType(OfferType.A)
                        .approvedBenefits(List.of(BenefitType.PONTOS))
                        .rejectionReason(null)
                        .createdAt(LocalDateTime.now())
                        .build();
        when(repository.findAllByCustomerIdentification(customerId))
                .thenReturn(List.of(entity));
        when(mapper.toResponseFromEntity(entity))
                .thenReturn(response);

        List<ProposalResponse> result =
                service.findAllByUser(customerId);

        assertThat(result)
                .hasSize(1)
                .contains(response);

        verify(repository)
                .findAllByCustomerIdentification(customerId);

    }

}