document.addEventListener('DOMContentLoaded', function() {

    checkLoginStatus();

});

function checkLoginStatus() {

    if (localStorage.accessToken != null) {
        let loginButton = document.getElementById('loginButton');
        loginButton.style.display = 'none';
        let joinButton = document.getElementById('joinButton');
        joinButton.style.display = 'none';
    } else {
        let logoutButton = document.getElementById('logoutButton');
        logoutButton.style.display = 'none';
    }
}

async function logout() {

    const jwtToken = localStorage.getItem('accessToken');
    try {
        const response = await fetch('/api/v1/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtToken}`
            }
        });

        if (response.status === 401 || response.ok) {
            alert("로그아웃 되었습니다");
            localStorage.removeItem("accessToken");
            location.reload();
        }

    } catch (error) {
        //ignore
    }

}

// 스피너 표시 함수
function showSpinner() {
    document.getElementById('spinner-container').style.display = 'block';
}

// 스피너 숨기기 함수
function hideSpinner() {
    document.getElementById('spinner-container').style.display = 'none';
}