package com.neuma573.autoboard.policy.model.dto;

import com.neuma573.autoboard.policy.model.entity.Policy;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PolicyResponse {

    private String policyName;

    private String policy;

    private Long id;


    public static PolicyResponse of(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .policyName(policy.getPolicyName())
                .policy(policy.getPolicy())
                .build();
    }

}
