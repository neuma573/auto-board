package com.neuma573.autoboard.global.repository;

import com.neuma573.autoboard.global.model.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyName(String policyName);
}
