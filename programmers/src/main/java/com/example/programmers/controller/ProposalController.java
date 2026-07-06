package com.example.programmers.controller;

import com.example.programmers.domain.ProposalDomain;
import com.example.programmers.dto.request.ProposalAnalysis;
import com.example.programmers.dto.request.ProposalRequest;
import com.example.programmers.mapper.ProposalMapper;
import com.example.programmers.service.ProposalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/proposal")
public class ProposalController {

    private final ProposalService service;

    public ProposalController(ProposalService propostaService){
        this.service = propostaService;
    }

    @PostMapping
    public ResponseEntity<ProposalAnalysis> save(@RequestBody @Valid ProposalRequest request){
        ProposalDomain proposal = ProposalMapper.toDomain(request);
        ProposalAnalysis proposalAnalysis = this.service.createProposal(proposal);
        return  ResponseEntity.
                created(URI.create("/proposal" + request.proposalIdentification())).
                body(proposalAnalysis);
        //mudar o tipo no retorno
    }
}
