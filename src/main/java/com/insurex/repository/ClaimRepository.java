package com.insurex.repository;

import com.insurex.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    // Spring safely handles inner joins down to the user's authentication email field
    List<Claim> findByUser_Email(String email);
    long countByStatusIgnoreCase(String status);
    boolean existsByUser_EmailAndPolicyName(String email, String policyName);
}
