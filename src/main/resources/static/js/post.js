document.addEventListener('DOMContentLoaded', function() {
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

async function deletePost() {

    if (confirm("이 게시글을 삭제하시겠습니까?")) { // 사용자에게 삭제 확인 요청
        await checkAndRefreshToken();
        const recaptchaToken = await executeRecaptchaV3('post_delete');

        fetch('/api/v1/post?postId=' + postId, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${getToken()}`,
                'Recaptcha-Token': recaptchaToken,
                'Action-Name': 'post_delete',
                'Recaptcha-version': 'v3'
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

async function toggleLike(event) {
    showSpinner();
    event.preventDefault();
    await checkAndRefreshToken();
    const recaptchaToken = await executeRecaptchaV3('post_like');

    fetch('/api/v1/like/' + postId, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${getToken()}`,
            'Recaptcha-Token': recaptchaToken,
            'Action-Name': 'post_like',
            'Recaptcha-version': 'v3'
        }
    })
        .then(async response => {
            if (response.ok) {
                const data = await response.json();
                document.getElementById('ratingUp').innerText = data.data.likeCount;
                hideSpinner();
            } else {
                alert("추천할 수 없습니다");
                hideSpinner();
            }
        })
        .catch(error => hideSpinner());

}
