package com.example.programmers.dto.request;

import com.example.programmers.enums.BenefitType;

import java.util.List;

public record UpdateBenefitsRequest(
        List<BenefitType> benefits
) {}