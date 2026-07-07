package com.example.programmers.repository;

import com.example.programmers.entity.CardEntity;
import com.example.programmers.entity.ProposalLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, UUID> {


    List<CardEntity> findByCustomerIdentification(
            UUID customerIdentification
    );
}

