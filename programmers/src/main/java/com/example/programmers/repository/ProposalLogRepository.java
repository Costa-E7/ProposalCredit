package com.example.programmers.repository;

import com.example.programmers.entity.ProposalLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ProposalLogRepository extends JpaRepository<ProposalLogEntity, UUID> {
}
