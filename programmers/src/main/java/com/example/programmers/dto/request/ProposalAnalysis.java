package com.example.programmers.dto.request;

import com.example.programmers.enums.ProposalStatus;

public record ProposalAnalysis(
        ProposalStatus valid,
        String reason
) {}
