package com.example.programmers.controller;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.ProposalAnalysis;
import com.example.programmers.dto.request.ProposalRequest;
import com.example.programmers.dto.response.ProposalResponse;
import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.ProposalStatus;
import com.example.programmers.mapper.ProposalMapper;
import com.example.programmers.service.ProposalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/proposals")
public class ProposalController {

    private final ProposalService service;

    public ProposalController(ProposalService propostaService) {
        this.service = propostaService;
    }

    @PostMapping
    public ResponseEntity<ProposalResponse> save(@RequestBody @Valid ProposalRequest request) {
        ProposalDomain proposal = ProposalMapper.toDomain(request);
        ProposalResponse response = this.service.createProposal(proposal);
        return ResponseEntity.
                created(URI.create("/proposals"
                        + response.proposalId()
                )).
                body(response);
        //mudar o tipo no retorno
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalResponse> findOne(
            @PathVariable UUID id
    ) {

        ProposalResponse response = service.findOne(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/customer/{customerIdentification}")
    public ResponseEntity<List<ProposalResponse>> findAllByUser(
            @PathVariable String customerIdentification
    ) {

        List<ProposalResponse> response = service.findAllByUser(customerIdentification);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProposalResponse>> findAllByStatus(
            @PathVariable ProposalStatus status
    ) {
        List<ProposalResponse> response =
                service.findAllByStatus(status);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/customer/{customerIdentification}/status/{status}")
    public ResponseEntity<List<ProposalResponse>> findAllByUserAndStatus(
            @PathVariable String customerIdentification,
            @PathVariable ProposalStatus status
    ) {
        List<ProposalResponse> response =
                service.findAllByUserAndStatus(
                        customerIdentification,
                        status
                );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/benefits")
    public ResponseEntity<ProposalResponse> updateBenefits(
            @PathVariable UUID id,
            @RequestBody List<BenefitType> benefits
    ) {
        ProposalResponse response = service.updateBenefits(id, benefits);
        return ResponseEntity.ok(response);
    }
}

