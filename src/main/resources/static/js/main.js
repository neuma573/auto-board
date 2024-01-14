document.addEventListener('DOMContentLoaded', function() {
    // 로그인 상태를 확인하고, 필요에 따라 액세스 토큰을 갱신하는 함수
    function checkLoginStatus() {
        const accessToken = localStorage.getItem('accessToken');
        if (!accessToken) {
            // 액세스 토큰이 없으면 로그인 페이지로 리다이렉트
            window.location.href = '/login';
        } else {
            // 액세스 토큰이 있으면 유효성 검사 및 갱신 시도
            validateAndUpdateToken(accessToken);
        }
    }

    // 액세스 토큰의 유효성을 검사하고, 필요에 따라 갱신하는 함수
    function validateAndUpdateToken(accessToken) {
        fetch('/api/v1/auth/validate', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${accessToken}`
            }
        })
            .then(response => {
                if (response.status === 200) {
                    console.log('Access token is valid');
                } else if (response.status === 401) {
                    // 액세스 토큰이 만료되었으면 리

// 액세스 토큰이 만료되었으면 리프레시 토큰을 이용하여 새로운 액세스 토큰 요청
refreshToken();
} else {
    throw new Error('Invalid response from server');
}
})
.catch(error => {
    console.error('Error:', error);
    logoutUser(); // 에러 발생 시 로그아웃 처리
});
}

// 리프레시 토큰을 이용해 액세스 토큰을 갱신하는 함수
function refreshToken() {
    fetch('/api/v1/auth/refresh', {
        method: 'POST',
        credentials: 'include' // 쿠키에 저장된 리프레시 토큰을 서버에 전송하기 위함
    })
        .then(response => {
            if (response.status === 200) {
                return response.json();
            } else {
                throw new Error('Failed to refresh token');
            }
        })
        .then(data => {
            localStorage.setItem('accessToken', data.data.accessToken);
            console.log('Access token refreshed');
        })
        .catch(error => {
            console.error('Error:', error);
            logoutUser(); // 토큰 갱신 실패 시 로그아웃 처리
        });
    }

    // 사용자를 로그아웃 처리하는 함수
    function logoutUser() {
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
    }

});
