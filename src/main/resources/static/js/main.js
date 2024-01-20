document.addEventListener('DOMContentLoaded', function() {

    checkLoginStatus();
    fetchBoards();
});

function checkLoginStatus() {

    if (getToken() != null) {
        let loginButton = document.getElementById('loginButton');
        loginButton.style.display = 'none';
        let joinButton = document.getElementById('joinButton');
        joinButton.style.display = 'none';
    } else {
        let logoutButton = document.getElementById('logoutButton');
        logoutButton.style.display = 'none';
        let writeButton = document.getElementById('writeButton');
        writeButton.style.display = 'none'
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
            localStorage.removeItem("accessToken");
            location.reload();
        }

    } catch (error) {
        //ignore
    }

}

// 게시판 데이터 가져오는 함수
async function fetchBoards() {
    try {
        const response = await fetch('/api/v1/board', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            updateBoardSelect(data.data);
        }

    } catch (error) {
        console.error('Error fetching boards:', error);
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

function updateBoardSelect(boards) {
    const select = document.getElementById('boardSelect');
    boards.forEach(board => {
        const option = document.createElement('option');
        option.value = board.id;
        option.textContent = `${board.boardName} (${board.postCount})`;
        select.appendChild(option);
        if (board.public) {
            select.value = board.id;
        }
    });
}

function getToken() {
    return localStorage.getItem('accessToken');
}