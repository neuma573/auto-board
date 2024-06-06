document.addEventListener('DOMContentLoaded', function() {
    checkLoginStatus();
});

document.querySelector('.navbar-brand').addEventListener('click', function() {
    // 현재 페이지 경로와 referrer를 가져옵니다.
    const currentPath = window.location.pathname;
    const referrer = document.referrer;

    // URL 객체를 사용하여 referrer의 경로를 추출합니다.
    const referrerPath = new URL(referrer).pathname;

    // 현재 경로가 '/main' 또는 '/' 이고, 이전 페이지가 같은 경로일 때만 localStorage 값을 설정합니다.
    if ((currentPath === '/main' || currentPath === '/') && referrerPath === currentPath) {
        localStorage.setItem('pageNumber', '1');
    }
});


async function executeRecaptchaV3(action) {
    return new Promise((resolve, reject) => {
        grecaptcha.ready(async () => {
            try {
                const token = await grecaptcha.execute('6LeZG34pAAAAAGhUyxO3RmnFcwbw9xVIVXg-1GAF', {action: action});
                resolve(token);
            } catch (error) {
                reject(error);
            }
        });
    });
}


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
    document.getElementById('logoutButton').style.display = '';
    document.getElementById('myPage').style.display = '';
}

function showLoginAndJoinButtons() {
    document.getElementById('myPage').style.display = 'none';
    document.getElementById('logoutButton').style.display = 'none';
    document.getElementById('loginButton').style.display = '';
    document.getElementById('joinButton').style.display = '';
    document.getElementById('loginButton').href = `/login?redirect=${encodeURIComponent(window.location.href)}`;
}

async function checkAndRefreshToken() {
    const accessToken = getToken();
    if(!accessToken) return ;
    if ( !(await verifyToken(accessToken))) {
        const isRefreshed = await refreshAccessToken();
        if (!isRefreshed) {
            deleteToken();
            window.location.href = `/login?redirect=${encodeURIComponent(window.location.href)}`; // 로그인 페이지로 리다이렉션
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
            return false;
        }
    } catch (error) {
        return false;
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
    localStorage.clear();
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

async function oauthLogin(token) {
    const response = await fetch('/api/v1/auth/verify-token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({token})
    });
    const data = await response.json();
    return data.status === 200 && data.data;
}
