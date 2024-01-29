
const postId = localStorage.getItem("dest");
document.addEventListener('DOMContentLoaded', () => {
    setupFormSubmit();
});

function getBoardIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('boardId');
}

function setupFormSubmit() {
    const form = document.querySelector('form');
    form.addEventListener('submit', handleFormSubmit);

    if (mode === 'modify') {
        getPostData();
    }

}

async function getPostData() {
    showSpinner();
    if (!postId) {
        alert('수정 중 문제가 발생했습니다.')
        location.href = '/main';
    }

    try {
        const response = await fetch(`/api/v1/post?postId=${postId}`);
        const data = await response.json();

        if (response.ok) {
            appendPostContent(data.data);
        } else {
            hideSpinner();
            throw new Error(data.message || "Failed to load post");
        }
    } catch (error) {
        hideSpinner();
        console.error("Error fetching post:", error);
    }
}

function appendPostContent(postData) {
    document.getElementById("title").value = postData.title;
    document.getElementById("content").textContent = postData.content;
    hideSpinner();
}


async function handleFormSubmit(event) {
    showSpinner();
    event.preventDefault();
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    const boardId = getBoardIdFromUrl();

    try {
        if (mode === 'modify') {
            await updatePost({ title, content, postId }); // 글 수정 로직
        } else {
            await submitPost({ title, content, boardId }); // 글 작성 로직
        }
        // 성공적으로 처리 후 리다이렉션
        window.location.href = '/main';
    } catch (error) {
        hideSpinner();
        console.error('Error:', error);
    }
}

async function updatePost(postData) {
    await checkAndRefreshToken(); // 토큰 체크 및 갱신
    const response = await fetch('/api/v1/post', {
        method: 'PUT', // PUT 메서드 사용
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}` // 적절한 토큰 설정
        },
        body: JSON.stringify(postData) // 요청 본문에 postData 포함
    });

    if (!response.ok) {
        const errorData = await response.json();
        alert(errorData.message);
        throw new Error('Failed to update post');
    }
    hideSpinner();
    return true;
}

async function submitPost(postData) {
    await checkAndRefreshToken();
    const response = await fetch('/api/v1/post', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify(postData)
    });

    if (!response.ok) {
        const errorData = await response.json();
        alert(errorData.message);
        throw new Error('Failed to submit post');
    }
    hideSpinner();
    return response.json();
}
