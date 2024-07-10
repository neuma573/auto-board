document.addEventListener('DOMContentLoaded', () => {
    const signupForm = document.getElementById('signupForm');

    signupForm.addEventListener('submit', handleSignupSubmit);
});

async function handleSignupSubmit(event) {
    showSpinner();
    event.preventDefault();

    const email = document.getElementById('email').value;
    const name = document.getElementById('name').value;
    const password = document.getElementById('password').value;
    const recaptchaResponse = document.getElementById('g-recaptcha-response').value;

    try {
        if (!validateRecaptcha(recaptchaResponse)) {
            hideSpinner();
            return;
        }

        if (!validateTermCheckbox()) {
            hideSpinner();
            alert('약관에 동의해주세요');
            return;
        }
        const agreeToConsentPolicy = document.getElementById("consentPolicyCheckbox").checked
        const agreeToTermOfUse = document.getElementById("termOfUseCheckbox").checked
        const response = await fetch('/api/v1/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Recaptcha-Token': recaptchaResponse,
                'Action-Name': 'signup',
                'Recaptcha-version': 'v2'
            },
            body: JSON.stringify({ email, name, password, agreeToConsentPolicy, agreeToTermOfUse })
        });
        const result = await response.json();
        if (!response.ok) {
            alert(result.message);
            grecaptcha.reset();
            throw new Error('Signup failed');
        }

        alert(`가입 신청이 완료되었으며 ${result.data.email} 이메일로 인증 링크가 전송되었습니다. 확인해주세요.`);

        // 성공 후 메인 페이지로 리디렉션
        hideSpinner();
        window.location.href = '/main';
    } catch (error) {
        hideSpinner();
        console.error('An error occurred:', error);
    }
}

async function checkEmail() {
    const email = document.getElementById('email').value;

    if (!email) {
        alert('이메일을 입력해주세요.');
        return;
    }

    try {
        const recaptchaToken = await executeRecaptchaV3('email_check');
        const response = await fetch(`/api/v1/users/email-check?email=${encodeURIComponent(email)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Recaptcha-Token': recaptchaToken,
                'Action-Name': 'email_check',
                'Recaptcha-version': 'v3'
            }
        });

        const result = await response.json();

        if (!response.ok) {
            alert(result.message);
            throw new Error('Email check failed');
        }

        if (result.data) {
            alert('사용 가능한 이메일입니다.');
            document.getElementById('submitButton').disabled = false;
            document.getElementById('email').readOnly = true;
        } else {
            alert('이미 사용 중인 이메일입니다.');
        }
    } catch (error) {
        console.error('An error occurred:', error);
    }
}

function validateRecaptcha(response) {
    if (!response) {
        alert('reCaptcha 챌린지를 수행해주세요');
        grecaptcha.reset();
        return false;
    }
    return true;
}

