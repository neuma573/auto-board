package com.neuma573.autoboard.global.service;

import com.neuma573.autoboard.global.exception.AccessDeniedException;
import com.neuma573.autoboard.global.model.dto.PolicyResponse;
import com.neuma573.autoboard.global.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    private final String TERM_OF_USE = "개인정보";

    @Transactional(readOnly = true)
    public PolicyResponse getTermOfUse() {
        return PolicyResponse.of(policyRepository.findByPolicyName(TERM_OF_USE).orElseThrow(AccessDeniedException::new));
    }
}
