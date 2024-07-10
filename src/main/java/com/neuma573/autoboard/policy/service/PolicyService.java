package com.neuma573.autoboard.policy.service;

import com.neuma573.autoboard.global.exception.AccessDeniedException;
import com.neuma573.autoboard.global.exception.NotFoundException;
import com.neuma573.autoboard.policy.model.dto.PolicyAgreementRequest;
import com.neuma573.autoboard.policy.model.dto.PolicyAgreementResponse;
import com.neuma573.autoboard.policy.model.dto.PolicyResponse;
import com.neuma573.autoboard.policy.model.entity.Policy;
import com.neuma573.autoboard.policy.model.entity.PolicyAgreement;
import com.neuma573.autoboard.policy.repository.PolicyAgreementRepository;
import com.neuma573.autoboard.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    private final PolicyAgreementRepository policyAgreementRepository;

    public static final String TERM_OF_USE = "이용약관";

    public static final String PRIVACY_POLICY = "개인정보 처리방침";

    public static final String CONSENT_AGREEMENT = "개인정보 수집 및 이용에 대한 동의";

    @Transactional(readOnly = true)
    public PolicyResponse getTermOfUse() {
        return PolicyResponse.of(policyRepository.findFirstByPolicyNameOrderByCreatedAtDesc(TERM_OF_USE).orElseThrow(AccessDeniedException::new));
    }

    @Transactional(readOnly = true)
    public PolicyResponse getPrivacyPolicy() {
        return PolicyResponse.of(policyRepository.findFirstByPolicyNameOrderByCreatedAtDesc(PRIVACY_POLICY).orElseThrow(AccessDeniedException::new));
    }

    @Transactional(readOnly = true)
    public PolicyResponse getConsentPolicy() {
        return PolicyResponse.of(policyRepository.findFirstByPolicyNameOrderByCreatedAtDesc(CONSENT_AGREEMENT).orElseThrow(AccessDeniedException::new));
    }

    @Transactional(readOnly = true)
    public Policy getPolicyById(Long policyId) {
        return policyRepository.findById(policyId).orElseThrow(() -> new NotFoundException("정책이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public Long getLatestPolicyId(String policyName) {
        return policyRepository.findFirstByPolicyNameOrderByCreatedAtDesc(policyName)
                .map(Policy::getId)
                .orElseThrow(() -> new NotFoundException("정책이 존재하지 않습니다."));
    }

    @Transactional
    public PolicyAgreementResponse submitPolicyAgreement(PolicyAgreementRequest policyAgreementRequest) {
        Policy policy = getPolicyById(policyAgreementRequest.getPolicyId());
        policyAgreementRepository.findByAgreedByAndAgreedFor(policyAgreementRequest.getUser(), policy).ifPresent(existingAgreement -> {
            throw new IllegalStateException("이미 동의된 정책입니다.");
        });
        PolicyAgreement policyAgreement = policyAgreementRepository.save(policyAgreementRequest.toEntity(policy));
        log.info("동의자: {}, 동의 정책: {}, 동의 일자: {}", policyAgreement.getAgreedBy().getId(), policyAgreement.getAgreedFor().getId(), policyAgreement.getAgreedAt());
        return PolicyAgreementResponse.of(policyAgreement);
    }
}
