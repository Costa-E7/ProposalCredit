package com.example.programmers.repository;

import com.example.programmers.entity.ProposalEntity;
import com.example.programmers.enums.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface  ProposalRepository extends JpaRepository<ProposalEntity, UUID> {

    List<ProposalEntity> findAllByCustomerIdentification(
            String customerIdentification
    );

    List<ProposalEntity> findAllByStatus(
            ProposalStatus status
    );


    List<ProposalEntity> findAllByCustomerIdentificationAndStatus(
            String customerIdentification,
            ProposalStatus status
    );
}
