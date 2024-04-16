document.addEventListener('DOMContentLoaded', () => {
    const signupForm = document.getElementById('signupForm');

    signupForm.addEventListener('submit', handleOAuthSignupSubmit);
});

async function handleOAuthSignupSubmit(event) {
    showSpinner();
    event.preventDefault();

    const name = document.getElementById('name').value;
    const uuid = document.getElementById('uuid').value;
    const recaptchaResponse = document.getElementById('g-recaptcha-response').value;

    try {

        if (!validateRecaptcha(recaptchaResponse)) {
            hideSpinner();
            return;
        }
        const response = await fetch('/api/v1/oauth2/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Recaptcha-Token': recaptchaResponse,
                'Action-Name': 'signup',
                'Recaptcha-version': 'v2'
            },
            body: JSON.stringify({ name, uuid })
        });
        const result = await response.json();
        if (!response.ok) {
            alert(result.message);
            grecaptcha.reset();
            throw new Error('Signup failed');
        }

        alert(`가입이 완료되었습니다.`);

        // 성공 후 메인 페이지로 리디렉션
        hideSpinner();
        window.location.href = '/main';
    } catch (error) {
        hideSpinner();
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
