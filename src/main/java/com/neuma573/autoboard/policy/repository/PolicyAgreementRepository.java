package com.neuma573.autoboard.policy.repository;

import com.neuma573.autoboard.policy.model.entity.Policy;
import com.neuma573.autoboard.policy.model.entity.PolicyAgreement;
import com.neuma573.autoboard.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyAgreementRepository extends JpaRepository<PolicyAgreement, Long> {

    Optional<PolicyAgreement> findByAgreedByAndAgreedFor(User agreedBy, Policy AgreedFor);
}