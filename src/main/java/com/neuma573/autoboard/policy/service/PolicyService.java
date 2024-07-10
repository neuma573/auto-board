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
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    private final UserService userService;

    private final PolicyAgreementRepository policyAgreementRepository;

    private final String TERM_OF_USE = "이용약관";

    private final String PRIVACY_POLICY = "개인정보 처리방침";

    private final String CONSENT_TO_COLLECTION_AND_USE_OF_PERSONAL_INFORMATION = "개인정보 수집 및 이용에 대한 동의";

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
        return PolicyResponse.of(policyRepository.findFirstByPolicyNameOrderByCreatedAtDesc(CONSENT_TO_COLLECTION_AND_USE_OF_PERSONAL_INFORMATION).orElseThrow(AccessDeniedException::new));
    }

    @Transactional(readOnly = true)
    public Policy getPolicyById(Long policyId) {
        return policyRepository.findById(policyId).orElseThrow(() -> new NotFoundException("정책이 존재하지 않습니다."));
    }

    @Transactional
    public PolicyAgreementResponse submitPolicyAgreement(PolicyAgreementRequest policyAgreementRequest) {
        User user = userService.getUserById(policyAgreementRequest.getUserId());
        Policy policy = getPolicyById(policyAgreementRequest.getPolicyId());
        policyAgreementRepository.findByAgreedByAndAgreedFor(user, policy).ifPresent(existingAgreement -> {
            throw new IllegalStateException("이미 동의된 정책입니다.");
        });
        PolicyAgreement policyAgreement = policyAgreementRepository.save(policyAgreementRequest.toEntity(user, policy));

        return PolicyAgreementResponse.of(policyAgreement);
    }
}
