package com.neuma573.autoboard.policy.model.dto;

import com.neuma573.autoboard.policy.model.entity.Policy;
import com.neuma573.autoboard.policy.model.entity.PolicyAgreement;
import com.neuma573.autoboard.user.model.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class PolicyAgreementRequest {

    private Long policyId;
    @Setter
    private Long userId;

    public PolicyAgreement toEntity(User user, Policy policy) {
        return PolicyAgreement.builder()
                .agreedBy(user)
                .isAgreed(true)
                .agreedFor(policy)
                .agreedAt(LocalDateTime.now())
                .build();
    }
}
