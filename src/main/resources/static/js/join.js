document.addEventListener('DOMContentLoaded', () => {
    const signupForm = document.getElementById('signupForm');
    signupForm.addEventListener('submit', handleSignupSubmit);
});

async function handleSignupSubmit(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const name = document.getElementById('name').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/api/v1/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, name, password })
        });
        const result = await response.json();
        if (!response.ok) {
            alert(result.message);
            throw new Error('Signup failed');
        }

        alert(`가입 신청이 완료되었으며 ${result.data.email} 이메일로 인증 링크가 전송되었습니다. 확인해주세요.`);

        // 성공 후 메인 페이지로 리디렉션
        window.location.href = '/main';
    } catch (error) {
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
        const response = await fetch(`/api/v1/users/email-check?email=${encodeURIComponent(email)}`);

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

