package com.neuma573.autoboard.policy.repository;

import com.neuma573.autoboard.policy.model.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findFirstByPolicyNameOrderByCreatedAtDesc(String policyName);
}
