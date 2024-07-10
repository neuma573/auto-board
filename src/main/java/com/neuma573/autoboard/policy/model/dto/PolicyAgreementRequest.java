package com.neuma573.autoboard.policy.model.dto;

import com.neuma573.autoboard.policy.model.entity.Policy;
import com.neuma573.autoboard.policy.model.entity.PolicyAgreement;
import com.neuma573.autoboard.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PolicyAgreementRequest {

    private Long policyId;
    @Setter
    private User user;

    public PolicyAgreement toEntity(Policy policy) {
        return PolicyAgreement.builder()
                .agreedBy(user)
                .isAgreed(true)
                .agreedFor(policy)
                .agreedAt(LocalDateTime.now())
                .build();
    }
}
