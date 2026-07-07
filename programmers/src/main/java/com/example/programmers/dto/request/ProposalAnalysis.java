package com.example.programmers.dto.request;


import com.example.programmers.enums.ProposalStatus;
import lombok.Getter;


public record ProposalAnalysis(
        ProposalStatus valid,
        String reason
) {}
