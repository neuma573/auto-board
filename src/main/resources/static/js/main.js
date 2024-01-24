document.addEventListener('DOMContentLoaded', function() {
    checkLoginStatus();
});

function checkLoginStatus() {
    const accessToken = getToken();
    if (accessToken != null) {
        verifyToken(accessToken).then(isValid => {
            if (isValid) {
                // Token is valid
                hideLoginAndJoinButtons();
            } else {
                // Token is invalid or expired
                refreshAccessToken().then(() => {
                    hideLoginAndJoinButtons();
                }).catch(() => {
                    // Handle error, for example, by redirecting to login page
                    showLoginAndJoinButtons();
                });
            }
        });
    } else {
        showLoginAndJoinButtons();
    }
}

function hideLoginAndJoinButtons() {
    document.getElementById('loginButton').style.display = 'none';
    document.getElementById('joinButton').style.display = 'none';
}

function showLoginAndJoinButtons() {
    document.getElementById('logoutButton').style.display = 'none';
}

async function checkAndRefreshToken() {
    const accessToken = getToken();
    if(!accessToken) return ;
    if ( !(await verifyToken(accessToken))) {
        const isRefreshed = await refreshAccessToken();
        if (!isRefreshed) {
            window.location.href = '/login'; // 로그인 페이지로 리다이렉션
        }
    }
}

async function verifyToken(accessToken) {
    if(accessToken == null) {
        localStorage.removeItem("accessToken");
        return ;
    }

    try {
        const response = await fetch('/api/v1/auth/verify-token', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify({ accessToken })
        });
        const data = await response.json();
        return data.status === 200 && data.data; // Assuming the response includes success status and payload
    } catch (error) {
        alert('세션이 만료되어 로그아웃됩니다.');
        deleteToken();
        window.location.href = '/login';
        return false;
    }
}

async function refreshAccessToken() {
    try {
        const response = await fetch('/api/v1/auth/refresh/token', {
            method: 'PUT',
            // Add necessary headers, credentials, or other configurations
        });
        const data = await response.json();
        if (data.status === 200) {
            // Update the token in the client
            setToken(data.data.accessToken);
            return true;
        } else {
            alert('세션이 만료되어 로그아웃됩니다.');
            deleteToken();
            window.location.href = '/login';
        }
    } catch (error) {
        alert('세션이 만료되어 로그아웃됩니다.');
        deleteToken();
        window.location.href = '/login';
    }
}

async function logout() {
    try {
        const response = await fetch('/api/v1/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.status === 401 || response.status === 500 || response.ok) {
            alert("로그아웃 되었습니다");
            deleteToken();
            location.reload();
        }

    } catch (error) {
        //ignore
    }

}

// 스피너 표시 함수
function showSpinner() {
    document.getElementById('spinner-container').style.display = '';
}

// 스피너 숨기기 함수
function hideSpinner() {
    document.getElementById('spinner-container').style.display = 'none';
}

function getToken() {
    return localStorage.getItem('accessToken');
}

function setToken(value) {
    return localStorage.setItem('accessToken', value);
}

function deleteToken() {
    localStorage.removeItem("accessToken");
}

function getFormattedCreatedAt(createdAt) {
    const now = new Date();
    const createdAtDate = new Date(createdAt);
    const yesterday = new Date(now);
    yesterday.setDate(yesterday.getDate() - 1);

    if (createdAtDate > yesterday) {
        return createdAtDate.toLocaleTimeString([], { hour12: false, hour: '2-digit', minute: '2-digit' });
    } else {
        return createdAtDate.toLocaleDateString([], { year: '2-digit', month: '2-digit', day: '2-digit' });
    }
}
