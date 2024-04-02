
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
        alert(error);
        console.error("Error fetching post:", error);
    }
}

function appendPostContent(postData) {
    document.getElementById("title").value = postData.title;
    if (editorInstance) {
        editorInstance.setData(postData.content);
    }
    hideSpinner();
}


async function handleFormSubmit(event) {
    showSpinner();
    event.preventDefault();
    const title = document.getElementById('title').value;
    const content = editorInstance.getData();
    const boardId = getBoardIdFromUrl();
    try {
        if (mode === 'modify') {
            await updatePost({ title, content, postId }); // 글 수정 로직
        } else {
            await submitPost({ title, content, boardId }); // 글 작성 로직
        }
    } catch (error) {
        hideSpinner();
        console.error('Error:', error);
    }
}

async function updatePost(postData) {
    await checkAndRefreshToken(); // 토큰 체크 및 갱신
    const recaptchaToken = document.getElementById('g-recaptcha-response').value;
    if(recaptchaToken === null || recaptchaToken === '') {
        alert('reCaptcha 챌린지를 수행해주세요');
        hideSpinner();
        return ;
    }
    const response = await fetch('/api/v1/post', {
        method: 'PUT', // PUT 메서드 사용
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`,
            'Recaptcha-Token': recaptchaToken,
            'Action-Name': 'post_put',
            'Recaptcha-version': 'v2'
        },
        body: JSON.stringify(postData) // 요청 본문에 postData 포함
    });

    if (!response.ok) {
        const errorData = await response.json();
        alert(errorData.message);
        grecaptcha.reset();
        throw new Error('Failed to update post');
    }
    hideSpinner();
    window.location.href = '/';
    return true;
}

async function submitPost(postData) {
    await checkAndRefreshToken();
    const recaptchaToken = document.getElementById('g-recaptcha-response').value;
    if(recaptchaToken === null || recaptchaToken === '') {
        alert('reCaptcha 챌린지를 수행해주세요');
        hideSpinner();
        return ;
    }
    const response = await fetch('/api/v1/post', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`,
            'Recaptcha-Token': recaptchaToken,
            'Action-Name': 'post_post',
            'Recaptcha-version': 'v2'
        },
        body: JSON.stringify(postData)
    });

    if (!response.ok) {
        const errorData = await response.json();
        alert(errorData.message);
        grecaptcha.reset();
        throw new Error('Failed to submit post');
    }
    hideSpinner();
    window.location.href = '/';
    return response.json();
}
