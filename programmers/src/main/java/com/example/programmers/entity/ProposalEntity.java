package com.example.programmers.entity;

import com.example.programmers.enums.BenefitType;
import com.example.programmers.enums.OfferType;
import com.example.programmers.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "proposal")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String customerIdentification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferType offerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @Column(length = 500)
    private String rejectionReason;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "proposal_benefit",
            joinColumns = @JoinColumn(name = "id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "benefit")
    @Builder.Default
    private List<BenefitType> benefits = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column()
    private LocalDateTime updatedAt;

    public void setBenefits(List<BenefitType> benefits) {
        this.benefits = benefits;
    }

    public void setUpdatedAt(){
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(ProposalStatus status) {
        this.status = status;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}