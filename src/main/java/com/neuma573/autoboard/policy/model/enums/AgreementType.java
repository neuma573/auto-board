package com.neuma573.autoboard.policy.model.enums;

public enum AgreementType {
    TERM_OF_USE("이용약관"),
    PRIVACY_POLICY("개인정보 처리방침"),
    CONSENT_AGREEMENT("개인정보 수집 및 이용에 대한 동의");

    private final String name;

    AgreementType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
