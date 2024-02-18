package com.neuma573.autoboard.global.model.dto;

import com.neuma573.autoboard.global.model.entity.Policy;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PolicyResponse {

    private String policyName;

    private String policy;


    public static PolicyResponse of(Policy policy) {
        return PolicyResponse.builder()
                .policyName(policy.getPolicyName())
                .policy(policy.getPolicy())
                .build();
    }

}
