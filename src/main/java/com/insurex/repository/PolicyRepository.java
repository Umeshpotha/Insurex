package com.insurex.repository;

import com.insurex.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
}
