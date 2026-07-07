package com.example.programmers.entity;

import com.example.programmers.enums.ProposalAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proposal_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalLogEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private ProposalEntity proposal;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String requestPayload;

    @Column(nullable = false)
    @Builder.Default
    private final LocalDateTime createdAt = LocalDateTime.now();

    private ProposalAction action;
}