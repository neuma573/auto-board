// 페이지 로드 시 댓글 불러오기
document.addEventListener('DOMContentLoaded', function() {
    loadPage(1);
});

function loadPage(pageNumber) {
    const currentUserEmail = atob(localStorage.getItem("user"));
    fetch(`/api/v1/comment/list?postId=${postId}&page=${pageNumber}`)
        .then(response => response.json())
        .then(data => {
            const totalPages = data.data.totalPages; // 서버로부터 전체 페이지 수 받아오기

            // 페이지네이션 버튼 생성
            generatePagination(totalPages);
            let activeCommentsCount = 0;
            // 댓글 목록 생성
            const comments = data.data.content;
            const commentsList = document.getElementById('comments-list');
            commentsList.innerHTML = ''; // 기존 댓글 목록 초기화

            comments.forEach(comment => {
                const commentElement = document.createElement('div');
                commentElement.className = 'card mb-1';
                commentElement.id = `comment-container-${comment.id}`;

                const cardBody = document.createElement('div');
                cardBody.className = 'card-body';

                const subtitle = document.createElement('h6');
                subtitle.className = 'card-subtitle mb-1 text-muted';

                if (comment.deleted) {
                    subtitle.textContent = `(삭제된 댓글입니다) ${comment.createdBy.name} (${comment.createdAt})`;
                } else {
                    activeCommentsCount++; // 활성 댓글 수 증가
                    subtitle.textContent = `${comment.createdBy.name} (${comment.createdAt})`;
                }

                const commentText = document.createElement('p');
                commentText.className = 'card-text';
                commentText.id = `comment-content-${comment.id}`;
                commentText.appendChild(document.createTextNode(comment.content)); // XSS 방지

                cardBody.appendChild(subtitle);
                cardBody.appendChild(commentText);

                if (comment.createdBy.email === currentUserEmail && !comment.deleted) {
                    const editButton = document.createElement('button');
                    editButton.className = 'btn btn-sm btn-success';
                    editButton.textContent = '수정';
                    editButton.setAttribute('onclick', `editCommentForm('${comment.id}', '${comment.createdBy.name} (${comment.createdAt})')`);

                    const deleteButton = document.createElement('button');
                    deleteButton.className = 'btn btn-sm btn-danger';
                    deleteButton.textContent = '삭제';
                    deleteButton.setAttribute('onclick', `deleteComment(${comment.id})`);

                    cardBody.appendChild(editButton);
                    cardBody.appendChild(deleteButton);
                }
                if (currentUserEmail !== `\x9Eée` && !comment.deleted) {
                    const replyButton = document.createElement('button');
                    replyButton.className = 'btn btn-sm btn-primary';
                    replyButton.textContent = '답글';
                    replyButton.setAttribute('onclick', `replyComment(${postId}, ${comment.id}, '${comment.createdBy.name}')`);

                    cardBody.appendChild(replyButton);
                }

                if (comment.childCount !== 0) {
                    const showReply = document.createElement('a');
                    const replySpan = document.createElement('span');
                    replySpan.textContent = `ㄴ 답글 ${comment.childCount} 개 더보기`;
                    replySpan.className = 'reply-text';

                    showReply.addEventListener('click', function(event) {
                        event.preventDefault();
                        loadReply(comment.id);
                    });

                    showReply.appendChild(replySpan);
                    cardBody.appendChild(showReply);
                }

                commentElement.appendChild(cardBody);
                commentsList.appendChild(commentElement);
                const replyArea = document.createElement('div');
                replyArea.dataset.commentId = comment.id;
                commentsList.appendChild(replyArea);

            });

            document.getElementById('comment-count').textContent = `(${data.data.totalElements})`; // 활성 댓글 수 업데이트
        })
        .catch(error => {
            console.error('Error loading comments:', error);
        });
}

// 댓글 작성
async function saveComment() {
    await checkAndRefreshToken();
    const content = document.getElementById('comment-content').value;
    const commentData = {postId, content};
    const recaptchaToken = await executeRecaptchaV3('comment_post');

    fetch('/api/v1/comment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`,
            'Recaptcha-Token': recaptchaToken,
            'Action-Name': 'comment_post',
            'Recaptcha-version': 'v3'
        },
        body: JSON.stringify(commentData)
    })
        .then(response => response.json()) // 응답을 JSON으로 변환
        .then(data => {
            if (data.status === 200) {
                document.getElementById('comment-content').value = ''; // 댓글 입력란 초기화
                loadPage(1); // 첫 페이지의 댓글 목록 다시 불러오기
            } else {
                console.log(data);
                alert(data.message); // data 객체에서 메시지를 알림으로 표시
            }
        })
        .catch(error => console.error('Error:', error));
}

function generatePagination(totalPages) {
    const paginationUl = document.getElementById('pagination');
    paginationUl.innerHTML = ''; // 기존의 페이징 버튼을 초기화

    for (let i = 1; i <= totalPages; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = 'page-item';

        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = '#';
        pageLink.textContent = i;

        // 페이지 링크 클릭 이벤트
        pageLink.addEventListener('click', function(e) {
            e.preventDefault();
            loadPage(i); // 해당 페이지 번호로 댓글을 로드하는 함수
        });

        pageItem.appendChild(pageLink);
        paginationUl.appendChild(pageItem);
    }
}

function editCommentForm(commentId, createdBy) {
    const commentContainer = document.getElementById(`comment-container-${commentId}`);
    const originalContent = document.getElementById(`comment-content-${commentId}`).textContent;

    const editFormHtml = `
        <textarea id="edit-content-${commentId}" class="form-control" rows="3">${originalContent}</textarea>
        <button onclick="submitEdit(${commentId})" class="btn btn-primary mt-2">저장</button>
        <button onclick="cancelEdit(${commentId}, '${originalContent}', '${createdBy}')" class="btn btn-secondary mt-2">취소</button>`;


    commentContainer.innerHTML = editFormHtml;
}

function cancelEdit(commentId, originalContent, createdBy) {
    const commentContainer = document.getElementById(`comment-container-${commentId}`);


    const cardBody = document.createElement('div');
    cardBody.className = 'card-body';

    const subtitle = document.createElement('h6');
    subtitle.className = 'card-subtitle mb-1 text-muted';
    subtitle.textContent = `${createdBy})`;

    const commentText = document.createElement('p');
    commentText.className = 'card-text';
    commentText.id = `comment-content-${commentId}`;
    commentText.appendChild(document.createTextNode(originalContent)); // XSS 방지

    cardBody.appendChild(subtitle);
    cardBody.appendChild(commentText);
    const editButton = document.createElement('button');
    editButton.className = 'btn btn-sm btn-success';
    editButton.textContent = '수정';
    editButton.setAttribute('onclick', `editCommentForm('${commentId}', '${createdBy}')`);

    const deleteButton = document.createElement('button');
    deleteButton.className = 'btn btn-sm btn-danger';
    deleteButton.textContent = '삭제';
    deleteButton.setAttribute('onclick', `deleteComment(${commentId})`);

    cardBody.appendChild(editButton);
    cardBody.appendChild(deleteButton);

    commentContainer.innerHTML = '';

    commentContainer.appendChild(cardBody);
}

async function submitEdit(commentId) {
    await checkAndRefreshToken();
    const editedContent = document.getElementById(`edit-content-${commentId}`).value;
    const commentData = {
        commentId: commentId,
        postId: postId, // postId는 현재 페이지의 게시글 ID
        content: editedContent
    };
    const recaptchaToken = await executeRecaptchaV3('comment_put');
    fetch('/api/v1/comment', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`,
            'Recaptcha-Token': recaptchaToken,
            'Action-Name': 'comment_put',
            'Recaptcha-version': 'v3'
        },
        body: JSON.stringify(commentData)
    })
        .then(response => {
            if (response.ok) {
                loadPage(1); // 댓글 목록을 다시 로드
            } else {
                alert('댓글 수정에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

async function deleteComment(commentId) {
    await checkAndRefreshToken();
    const recaptchaToken = await executeRecaptchaV3('comment_delete');
    if (confirm("이 댓글을 삭제하시겠습니까?")) {
        fetch(`/api/v1/comment?commentId=${commentId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`,
                'Recaptcha-Token': recaptchaToken,
                'Action-Name': 'comment_delete',
                'Recaptcha-version': 'v3'
            }
        })
            .then(response => {
                if (response.ok) {
                    loadPage(1); // 댓글 목록을 다시 로드
                } else {
                    alert('댓글 삭제에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
}

function replyComment(postId, commentId, username) {
    if (document.getElementById(`reply-form-${commentId}`)) {
        document.getElementById(`reply-form-${commentId}`).remove()
        return;
    }

    const form = document.createElement('form');
    form.id = `reply-form-${commentId}`;
    form.className = 'mt-2 reply-form';

    const textarea = document.createElement('textarea');
    textarea.className = 'form-control mb-2';
    textarea.placeholder = `${username} 에게 답글을 작성합니다`;
    textarea.rows = 3;

    const submitButton = document.createElement('button');
    submitButton.type = 'button';
    submitButton.className = 'btn btn-sm btn-primary mt-2';
    submitButton.textContent = '답글 작성';
    submitButton.onclick = function () {
        submitReply(postId, commentId, textarea.value);
    };

    form.appendChild(textarea);
    form.appendChild(submitButton);

    const commentContainer = document.getElementById(`comment-container-${commentId}`);
    commentContainer.appendChild(form);
}

async function submitReply(postId, parentId, content) {
    await checkAndRefreshToken();
    const commentData = {postId, parentId, content};
    const recaptchaToken = await executeRecaptchaV3('comment_post');

    fetch('/api/v1/comment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`,
            'Recaptcha-Token': recaptchaToken,
            'Action-Name': 'comment_post',
            'Recaptcha-version': 'v3'
        },
        body: JSON.stringify(commentData)
    })
        .then(response => response.json()) // 응답을 JSON으로 변환
        .then(data => {
            if (data.status === 200) {
                loadPage(1); // 첫 페이지의 댓글 목록 다시 불러오기
            } else {
                console.log(data);
                alert(data.message); // data 객체에서 메시지를 알림으로 표시
            }
        })
        .catch(error => console.error('Error:', error));
}

function loadReply(commentId, lastCommentId) {
    showSpinner();
    if (document.querySelector('div[data-comment-id="' + commentId + '"]').innerHTML.trim() !== "" && lastCommentId === undefined) {
        document.querySelector('div[data-comment-id="' + commentId + '"]').innerHTML = '';
        return;
    }

    let fetchUrl = `/api/v1/comment/replies?commentId=${commentId}`;

    const loadMoreElement = document.getElementById('loadMore' + commentId);
    if (loadMoreElement) {
        loadMoreElement.remove();
    }
    if (lastCommentId !== undefined) {
        fetchUrl += `&lastCommentId=${lastCommentId}`;
    }
    const currentUserEmail = atob(localStorage.getItem("user"));

    fetch(fetchUrl)
        .then(response => response.json())
        .then(data => {
            const repliesResponse = data.data;
            const commentDiv = document.querySelector(`[data-comment-id="${commentId}"]`);
            repliesResponse.replies.forEach(reply => {
                const replyElement = document.createElement('div');
                replyElement.className = 'card mb-1 reply-card';
                replyElement.id = 'comment-container-' + reply.id;

                const cardBody = document.createElement('div');
                cardBody.className = 'card-body';

                const subtitle = document.createElement('h6');
                subtitle.className = 'card-subtitle mb-1 text-muted';
                subtitle.textContent = `${reply.createdBy.name} (${reply.createdAt})`;

                const text = document.createElement('p');
                text.id = 'comment-content-' + reply.id;
                text.className = 'card-text';

                if (reply.deleted) {
                    text.textContent = '(삭제된 댓글입니다)';
                } else {
                    text.textContent = reply.content;
                }
                cardBody.appendChild(subtitle);
                cardBody.appendChild(text);



                if (reply.createdBy.email === currentUserEmail && !reply.deleted) {
                    const editButton = document.createElement('button');
                    editButton.className = 'btn btn-sm btn-success';
                    editButton.textContent = '수정';
                    editButton.setAttribute('onclick', `editCommentForm('${reply.id}', '${reply.createdBy.name} (${reply.createdAt})')`);

                    const deleteButton = document.createElement('button');
                    deleteButton.className = 'btn btn-sm btn-danger';
                    deleteButton.textContent = '삭제';
                    deleteButton.setAttribute('onclick', `deleteComment(${reply.id})`);

                    cardBody.appendChild(editButton);
                    cardBody.appendChild(deleteButton);
                }
                replyElement.appendChild(cardBody);
                commentDiv.appendChild(replyElement);
            });
            if (repliesResponse.hasMore) {
                const lastReply = repliesResponse.replies[repliesResponse.replies.length - 1];
                const loadMoreButton = document.createElement('button');
                loadMoreButton.className = 'btn btn-link reply-text';
                loadMoreButton.id = `loadMore${commentId}`
                loadMoreButton.textContent = '더보기';
                loadMoreButton.addEventListener('click', function() {
                    loadReply(commentId, lastReply.id);
                });
                commentDiv.appendChild(loadMoreButton);
            }
        })
        .catch(error => {
            console.error('Error loading replies:', error);
        });
    hideSpinner();
}
