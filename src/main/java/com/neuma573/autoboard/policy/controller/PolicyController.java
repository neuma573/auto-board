package com.neuma573.autoboard.policy.controller;

import com.neuma573.autoboard.policy.model.dto.PolicyAgreementRequest;
import com.neuma573.autoboard.policy.model.dto.PolicyAgreementResponse;
import com.neuma573.autoboard.policy.model.dto.PolicyResponse;
import com.neuma573.autoboard.global.model.dto.Response;
import com.neuma573.autoboard.global.utils.ResponseUtils;
import com.neuma573.autoboard.policy.service.PolicyService;
import com.neuma573.autoboard.security.utils.JwtProvider;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/policy")
@RequiredArgsConstructor
@RestController
public class PolicyController {

    private final PolicyService policyService;

    private final ResponseUtils responseUtils;

    private final UserService userService;

    private final JwtProvider jwtProvider;

    @GetMapping("/tos")
    public ResponseEntity<Response<PolicyResponse>> getTermOfUse() {
        return ResponseEntity.ok().body(responseUtils.success(policyService.getTermOfUse()));
    }

    @PostMapping("/agreement")
    public ResponseEntity<Response<PolicyAgreementResponse>> submitPolicyAgreement(@RequestBody PolicyAgreementRequest policyAgreementRequest,
                                                                                   HttpServletRequest httpServletRequest) {
        User user = userService.getUserById(jwtProvider.parseUserId(httpServletRequest));
        policyAgreementRequest.setUser(user);
        return ResponseEntity.ok().body(responseUtils.success(policyService.submitPolicyAgreement(policyAgreementRequest)));
    }
}
