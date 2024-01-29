document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        showSpinner();
        e.preventDefault();
        let email = document.getElementById('email').value;
        let password = document.getElementById('password').value;

        fetch('/api/v1/auth/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email, password: password }),
        })
            .then(response => {
                // status 코드에 따라 다른 처리를 합니다.
                if (response.status === 200) {
                    return response.json(); // 성공 응답 처리
                } else {
                    throw response; // 에러 응답 처리
                }
            })
            .then(data => {
                console.log('Success:', data);
                alert('로그인 성공:');
                localStorage.setItem("user", btoa(email));
                localStorage.setItem("accessToken", data.data.accessToken);
                window.location.href = "/";
            })
            .catch((error) => {
                error.json().then(errMessage => {
                    console.error('Error:', errMessage);
                    alert(errMessage.message); // 에러 메시지 표시
                });
            });
        hideSpinner();
    });
});