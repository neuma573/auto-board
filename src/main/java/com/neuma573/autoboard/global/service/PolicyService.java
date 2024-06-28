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

    private final String TERM_OF_USE = "이용약관";

    private final String PRIVACY_POLICY = "개인정보 처리방침";

    private final String CONSENT_TO_COLLECTION_AND_USE_OF_PERSONAL_INFORMATION = "개인정보 수집 및 이용에 대한 동의";

    @Transactional(readOnly = true)
    public PolicyResponse getTermOfUse() {
        return PolicyResponse.of(policyRepository.findByPolicyName(TERM_OF_USE).orElseThrow(AccessDeniedException::new));
    }

    @Transactional(readOnly = true)
    public PolicyResponse getPrivacyPolicy() {
        return PolicyResponse.of(policyRepository.findByPolicyName(PRIVACY_POLICY).orElseThrow(AccessDeniedException::new));
    }

    @Transactional(readOnly = true)
    public PolicyResponse getConsentPolicy() {
        return PolicyResponse.of(policyRepository.findByPolicyName(CONSENT_TO_COLLECTION_AND_USE_OF_PERSONAL_INFORMATION).orElseThrow(AccessDeniedException::new));
    }

}
