package com.neuma573.autoboard.policy.model.dto;

import com.neuma573.autoboard.policy.model.entity.PolicyAgreement;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PolicyAgreementResponse {
    private boolean isAgreed;

    private LocalDateTime agreedAt;

    public static PolicyAgreementResponse of(PolicyAgreement policyAgreement) {
        return PolicyAgreementResponse.builder()
                .isAgreed(policyAgreement.isAgreed())
                .agreedAt(policyAgreement.getAgreedAt())
                .build();
    }

}
