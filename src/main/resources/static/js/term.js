function validateTermCheckbox() {
    return document.getElementById('consentPolicyCheckbox').checked && document.getElementById('termOfUseCheckbox').checked;
}

function enableCheckboxOnScroll(policyBoxId, checkboxId) {
    const policyBox = document.getElementById(policyBoxId);
    const checkbox = document.getElementById(checkboxId);

    policyBox.addEventListener('scroll', function() {
        if (policyBox.scrollHeight - policyBox.scrollTop <= policyBox.clientHeight + 1) {
            checkbox.disabled = false;
        }
    });
}

enableCheckboxOnScroll('consentPolicy', 'consentPolicyCheckbox');
enableCheckboxOnScroll('termsOfUse', 'termOfUseCheckbox');