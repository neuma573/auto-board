document.addEventListener('DOMContentLoaded', function() {
    initializePost();
    checkPermission();

    const modifyButton = document.getElementById('modify');
    modifyButton.href = `/modify?postId=${postId}`;

    if (!localStorage.getItem('user')) {
        let commentForm = document.querySelector('.comment-form');
        if (commentForm) {
            commentForm.style.display = 'none';
        }
    }

});

async function initializePost() {
    showSpinner();
    if (!postId) {
        console.error("No post ID found");
        return;
    }

    try {
        const response = await fetch(`/api/v1/post?postId=${postId}`);
        const data = await response.json();

        if (response.ok) {
            updatePostContent(data.data);
        } else {
            throw new Error(data.message || "Failed to load post");

        }
    } catch (error) {
        hideSpinner();
        alert('error');
        location.href = '/';

    }
}

function updatePostContent(postData) {
    document.getElementById("title").textContent = postData.title;
    document.getElementById("user").textContent = postData.userResponse.name;
    document.getElementById("views").textContent = `조회 ${postData.views}`;
    document.getElementById("content").textContent = postData.content;

    const postDateElement = document.querySelector(".post-info time");
    postDateElement.textContent = `작성일 ${postData.createdAt}`;
    postDateElement.setAttribute("datetime", postData.createdAt);
    document.title = postData.title;
    hideSpinner();
}
function checkPermission() {
    fetch('/api/v1/post/permission?postId=' + postId)
        .then(response => {
            if (response.status === 401) {
                // 인증되지 않은 사용자의 경우 모든 버튼 숨기기
                toggleButton('modify', false);
                toggleButton('delete', false);
                return;
            }
            return response.json();
        })
        .then(data => {
            if (data) {
                const permissionData = data.data;
                toggleButton('modify', permissionData.ableToModify);
                toggleButton('delete', permissionData.ableToDelete);
            }
        })
        .catch(error => console.error('Error:', error));
}

function toggleButton(buttonId, isVisible) {
    const button = document.getElementById(buttonId);
    if (button) {
        button.style.display = isVisible ? '' : 'none';
    }
}

function deletePost() {

    if (confirm("이 게시글을 삭제하시겠습니까?")) { // 사용자에게 삭제 확인 요청
        fetch('/api/v1/post?postId=' + postId, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${getToken()}` // 적절한 토큰을 포함하여 인증
            }
        })
            .then(response => {
                if (response.ok) {
                    alert("게시글이 삭제되었습니다.");
                    window.location.href = '/main'; // 삭제 후 메인 페이지로 리디렉션
                } else {
                    alert("게시글 삭제에 실패하였습니다.");
                }
            })
            .catch(error => console.error('Error:', error));
    }
}
