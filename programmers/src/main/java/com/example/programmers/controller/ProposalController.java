package com.example.programmers.controller;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.ProposalRequest;
import com.example.programmers.dto.request.UpdateBenefitsRequest;
import com.example.programmers.dto.response.ProposalResponse;
import com.example.programmers.enums.ProposalStatus;
import com.example.programmers.mapper.ProposalMapper;
import com.example.programmers.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService service;

    @PostMapping
    public ResponseEntity<ProposalResponse> save(@RequestBody @Valid ProposalRequest request) {
        ProposalDomain proposal = ProposalMapper.toDomain(request);
        ProposalResponse response = this.service.createProposal(proposal);
        return ResponseEntity.
                created(URI.create("/proposals/"
                        + response.proposalId()
                )).
                body(response);
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
            @PathVariable UUID customerIdentification
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
            @PathVariable UUID customerIdentification,
            @PathVariable ProposalStatus status
    ) {
        List<ProposalResponse> response =
                service.findAllByUserAndStatus(
                        customerIdentification,
                        status
                );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/benefit")
    public ResponseEntity<ProposalResponse> updateBenefits(
            @PathVariable UUID id,
            @RequestBody UpdateBenefitsRequest updateBenefitsRequest
    ) {
        ProposalResponse response = service.updateBenefits(id, updateBenefitsRequest.benefits());
        return response != null
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }
}

