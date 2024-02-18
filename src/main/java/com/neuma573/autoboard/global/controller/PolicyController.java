package com.neuma573.autoboard.global.controller;

import com.neuma573.autoboard.global.model.dto.PolicyResponse;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.service.PolicyService;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/policy")
@RequiredArgsConstructor
@RestController
public class PolicyController {

    private final PolicyService policyService;

    private final ResponseUtils responseUtils;

    @GetMapping("/tos")
    public ResponseEntity<Response<PolicyResponse>> getTermOfUse() {
        return ResponseEntity.ok().body(responseUtils.success(policyService.getTermOfUse()));
    }
}
