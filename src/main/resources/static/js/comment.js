// 페이지 로드 시 댓글 불러오기
document.addEventListener('DOMContentLoaded', function() {
    loadPage(1);
});

// 댓글 불러오기
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
                const commentsHtml = comments.map(comment => {
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
                    // 현재 사용자가 댓글 작성자인 경우 수정/삭제 버튼 추가
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

                    commentElement.appendChild(cardBody);
                    return commentElement.outerHTML;

                }).join('');
                document.getElementById('comment-count').textContent = `(${activeCommentsCount})`; // 활성 댓글 수 업데이트
                document.getElementById('comments-list').innerHTML = commentsHtml;
            })
            .catch(error => {
                console.error('Error loading comments:', error);
            });
    }

// 댓글 작성
function saveComment() {
    checkAndRefreshToken().then(r => {
        const content = document.getElementById('comment-content').value;
        const commentData = {postId, content};

        fetch('/api/v1/comment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
            },
            body: JSON.stringify(commentData)
        })
            .then(response => {
                if (response.ok) {
                    document.getElementById('comment-content').value = ''; // 댓글 입력란 초기화
                    loadPage(1); // 첫 페이지의 댓글 목록 다시 불러오기
                } else {
                    alert('댓글 작성에 실패했습니다.');
                }
            })
    });

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

function submitEdit(commentId) {
    const editedContent = document.getElementById(`edit-content-${commentId}`).value;
    const commentData = {
        commentId: commentId,
        postId: postId, // postId는 현재 페이지의 게시글 ID
        content: editedContent
    };

    fetch('/api/v1/comment', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getToken()}`
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

function deleteComment(commentId) {
    if (confirm("이 댓글을 삭제하시겠습니까?")) {
        fetch(`/api/v1/comment?commentId=${commentId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
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
