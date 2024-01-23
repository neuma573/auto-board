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
}

async function handleFormSubmit(event) {
    showSpinner();
    event.preventDefault();
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    const boardId = getBoardIdFromUrl();

    try {
        await submitPost({ title, content, boardId });
        // 성공적으로 글이 작성되면, 예를 들어 메인 페이지로 이동
        window.location.href = '/main';
    } catch (error) {
        hideSpinner();
    }
}

async function submitPost(postData) {
    await checkAndRefreshToken();
    const response = await fetch('/api/v1/post', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}` // 토큰을 적절하게 설정하세요
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