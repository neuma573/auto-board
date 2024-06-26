let currentBoard = -1;

document.addEventListener('DOMContentLoaded', async function () {
    try {
        await fetchBoards();

        let boardId = localStorage.getItem('currentBoardId');
        let page = restorePage();

        await fetchPosts(boardId, page, 10, 'desc');
        if (localStorage.getItem("accessToken") === null) {
            document.getElementById('writeButton').style.display = 'none';
        }
    } catch (error) {
        console.error('Error after fetching boards:', error);
    }
});

async function fetchBoards() {
    showSpinner();
    await checkAndRefreshToken();
    let boardId = localStorage.getItem('currentBoardId');
    if (typeof boardId !== 'object' || boardId === null || Object.keys(boardId).length === 0) {
        boardId = 1;
        localStorage.setItem('currentBoardId', String(boardId));
    }
    try {
        const response = await fetch('/api/v1/board', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const { data } = await response.json();
            updateBoardSelect(data);
            const firstPublicBoard = data.find(board => board.public);


            if (localStorage.getItem('currentBoardId') === null) {
                if (firstPublicBoard) {
                    currentBoard = firstPublicBoard.id;
                }
            } else {
                currentBoard = localStorage.getItem('currentBoardId');
            }
            hideSpinner();
        }
    } catch (error) {
        hideSpinner();
        console.error('Error fetching boards:', error);
    }
}

function updateBoardSelect(boards) {
    const select = document.getElementById('boardSelect');
    select.innerHTML = '';
    let firstPublicBoardId = null;

    boards.forEach(board => {
        localStorage.setItem(board.id, board.postCount);
        const option = document.createElement('option');
        option.value = board.id;
        option.textContent = `${board.name} (${board.postCount})`;
        select.appendChild(option);
        if (board.public && firstPublicBoardId === null) {
            firstPublicBoardId = board.id;
        }
    });

    const storedBoardId = localStorage.getItem('currentBoardId');
    if (storedBoardId !== null) {
        currentBoard = storedBoardId;
        select.value = storedBoardId;
    } else {
        select.value = firstPublicBoardId;
        currentBoard = firstPublicBoardId;
        localStorage.setItem('currentBoardId', currentBoard);
    }

    document.getElementById('writeButton').href = `/write?boardId=${currentBoard}`;

    // 이벤트 리스너 추가
    select.addEventListener('change', handleBoardSelectChange);
}

function handleBoardSelectChange() {
    const selectedBoardId = document.getElementById('boardSelect').value;
    currentBoard = selectedBoardId;
    localStorage.setItem('currentBoardId', selectedBoardId);
    const writeButton = document.getElementById('writeButton');
    writeButton.href = `/write?boardId=${selectedBoardId}`;
    storeCurrentPage(1);
    fetchPosts(selectedBoardId, 1, 10, 'desc');
}

async function fetchPosts(boardId, page, size, order) {
    storeCurrentPage(page)
    showSpinner();
    await checkAndRefreshToken();

    try {
        const response = await fetch(`/api/v1/post/list?boardId=${boardId}&page=${page}&size=${size}&order=${order}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${getToken()}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            updatePostsTable(data.data); // 기존 테이블 업데이트
            updateMobilePostsList(data.data); // 모바일 화면용 리스트 업데이트

            const totalRecords = data.data.totalElements; // 백엔드로부터 받은 총 페이지 수
            createPaginationButtons(Math.ceil(totalRecords / size) , page);

            hideSpinner();
        } else {
            hideSpinner();
            throw new Error('Server responded with non-OK status');
        }
    } catch (error) {
        console.error('Error fetching posts:', error);
        // 에러 발생 시 localStorage에서 currentBoardId 삭제
        localStorage.removeItem('currentBoardId');
        // 게시판 재로딩
        currentBoard = -1;
        hideSpinner();
        await fetchBoards();
    }
}

function updatePostsTable(posts) {
    const tableBody = document.querySelector('.table tbody');
    tableBody.innerHTML = ''; // Clear existing rows

    if (posts.empty === true) {
        // 게시글이 없을 경우 표시할 행 추가
        const row = tableBody.insertRow();
        const cell = row.insertCell(0);
        cell.textContent = "게시글이 없습니다.";
        cell.setAttribute('colspan', 5); // 모든 컬럼을 합친 하나의 셀로 표시
        cell.style.textAlign = 'center';
    } else {
        const startIndex = posts.totalElements - posts.pageable.offset;
        posts.content.forEach((post, index) => {
            const row = tableBody.insertRow();
            const numberCell = row.insertCell(0);
            const titleCell = row.insertCell(1);
            const authorCell = row.insertCell(2);
            const dateCell = row.insertCell(3);
            const viewsCell = row.insertCell(4);
            const likesCell = row.insertCell(5);

            numberCell.classList.add("td-center");
            authorCell.classList.add("td-center");
            dateCell.classList.add("td-center");
            viewsCell.classList.add("td-center");
            likesCell.classList.add("td-center");
            const postNumber = startIndex - index;
            numberCell.textContent = postNumber;

            const titleLink = document.createElement('a');
            titleLink.href = '#';

            if (post.deleted) {
                titleLink.textContent = `[삭제됨] ${post.title}`;
                titleLink.style.fontStyle = 'italic'; // 이탤릭체 적용
            } else {
                titleLink.textContent = post.title;
            }

            if (post.commentCount !== 0) {
                const commentCountSpan = document.createElement('span');
                commentCountSpan.textContent = ` [${post.commentCount}]`;
                commentCountSpan.className = 'comment-count-span';
                titleLink.appendChild(commentCountSpan);
            }

            titleLink.onclick = function() { clickPost(post.id); };
            titleCell.appendChild(titleLink);

            if (post.hasImages) {
                const imageIcon = document.createElement('img');
                imageIcon.src = '/images/image.svg';
                imageIcon.alt = 'Image';
                imageIcon.className = 'image-icon';
                titleCell.appendChild(imageIcon);
            }

            if (post.hasVideo) {
                const imageIcon = document.createElement('img');
                imageIcon.src = '/images/video.svg';
                imageIcon.alt = 'Image';
                imageIcon.className = 'image-icon';
                titleCell.appendChild(imageIcon);
            }

            authorCell.textContent = post.userName;
            dateCell.textContent = getFormattedCreatedAt(post.createdAt);
            viewsCell.textContent = post.views;
            likesCell.textContent = post.likeCount === null ? 0 : post.likeCount;
        });
    }


}

function updateMobilePostsList(posts) {
    const listGroup = document.querySelector('.list-group');
    listGroup.innerHTML = '';

    if (posts.empty === true) {
        // 게시글이 없을 경우 표시할 항목 추가
        const listItem = document.createElement('div');
        listItem.className = 'list-group-item';
        listItem.textContent = "게시글이 없습니다.";
        listItem.style.textAlign = 'center';
        listGroup.appendChild(listItem);
    } else {
        posts.content.forEach(post => {
            const listItem = document.createElement('div');
            listItem.className = 'list-group-item';

            const postTitleLink = document.createElement('a');
            postTitleLink.href = '#';
            postTitleLink.className = 'post-title';



            if (post.deleted) {
                postTitleLink.textContent = `[삭제됨] ${post.title.substring(0, 30)}${post.title.length > 30 ? '...' : ''}`;
                postTitleLink.style.fontStyle = 'italic'; // 이탤릭체 적용
            } else {
                postTitleLink.textContent = `${post.title.substring(0, 30)}${post.title.length > 30 ? '...' : ''}`;
                if (post.hasImages) {
                    const imageIcon = document.createElement('img');
                    imageIcon.src = '/images/image.svg';
                    imageIcon.alt = 'Image';
                    imageIcon.className = 'image-icon';
                    postTitleLink.appendChild(imageIcon);
                }

                if (post.hasVideo) {
                    const imageIcon = document.createElement('img');
                    imageIcon.src = '/images/video.svg';
                    imageIcon.alt = 'Image';
                    imageIcon.className = 'image-icon';
                    postTitleLink.appendChild(imageIcon);
                }

            }

            if (post.commentCount !== 0) {

                const commentCountSpan = document.createElement('span');
                commentCountSpan.textContent = ` [${post.commentCount}]`;
                commentCountSpan.className = 'comment-count-span';

                postTitleLink.appendChild(commentCountSpan);
            }



            postTitleLink.onclick = function() { clickPost(post.id); };
            listItem.appendChild(postTitleLink);

            const postDetails = document.createElement('div');
            postDetails.className = 'post-details';

            const authorSpan = document.createElement('span');
            authorSpan.className = 'author';
            authorSpan.textContent = post.userName;
            postDetails.appendChild(authorSpan);

            const statsSpan = document.createElement('span');
            statsSpan.className = 'stats';
            statsSpan.textContent = `작성일: ${getFormattedCreatedAt(post.createdAt)} | 조회수: ${post.views} | 추천수: ${post.likeCount === null ? 0 : post.likeCount}`;
            postDetails.appendChild(statsSpan);

            listItem.appendChild(postDetails);
            listGroup.appendChild(listItem);
        });
    }
}

function clickPost(postId) {
    localStorage.setItem("dest", postId);
    location.href = `/post?postId=${localStorage.getItem("dest")}`;
}

function createPaginationButtons(totalPages, currentPage) {
    const paginationUl = document.querySelector('.pagination');
    paginationUl.innerHTML = ''; // 기존 페이징 버튼 삭제

    const pageGroupSize = 5; // 한 페이지 그룹에 표시할 페이지 수
    let startPage = Math.floor((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
    let endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

    // 첫 페이지로 이동하는 버튼 (첫 페이지 그룹이 아닐 때만 표시)
    if (startPage > pageGroupSize) {
        const firstPageItem = document.createElement('li');
        firstPageItem.className = 'page-item';
        const firstPageLink = document.createElement('a');
        firstPageLink.className = 'page-link';
        firstPageLink.href = '#';
        firstPageLink.textContent = '<<';
        firstPageLink.onclick = function() {
            fetchPosts(currentBoard, 1, 10, 'desc');
        };
        firstPageItem.appendChild(firstPageLink);
        paginationUl.appendChild(firstPageItem);
    }

    // 이전 페이지 그룹으로 이동하는 버튼 (현재 그룹이 첫 번째 그룹이 아닐 때)
    if (startPage > 1) {
        const previousGroupItem = document.createElement('li');
        previousGroupItem.className = 'page-item';
        const previousGroupLink = document.createElement('a');
        previousGroupLink.className = 'page-link';
        previousGroupLink.href = '#';
        previousGroupLink.textContent = '<';
        previousGroupLink.onclick = function() {
            fetchPosts(currentBoard, startPage - 1, 10, 'desc');
        };
        previousGroupItem.appendChild(previousGroupLink);
        paginationUl.appendChild(previousGroupItem);
    }

    // 페이지 번호 버튼
    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = `page-item ${i === currentPage ? 'active' : ''}`;
        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = '#';
        pageLink.textContent = i;
        pageLink.onclick = function() {
            fetchPosts(currentBoard, i, 10, 'desc');
        };

        pageItem.appendChild(pageLink);
        paginationUl.appendChild(pageItem);
    }

    if (endPage < totalPages) {
        const nextPageItem = document.createElement('li');
        nextPageItem.className = 'page-item';
        const nextPageLink = document.createElement('a');
        nextPageLink.className = 'page-link';
        nextPageLink.href = '#';
        nextPageLink.textContent = '>';
        nextPageLink.onclick = function() {
            fetchPosts(currentBoard, endPage + 1, 10, 'desc');
        };
        nextPageItem.appendChild(nextPageLink);
        paginationUl.appendChild(nextPageItem);
    }

    if (endPage < totalPages) {
        const lastPageItem = document.createElement('li');
        lastPageItem.className = 'page-item';
        const lastPageLink = document.createElement('a');
        lastPageLink.className = 'page-link';
        lastPageLink.href = '#';
        lastPageLink.textContent = '>>';
        lastPageLink.onclick = function() {
            fetchPosts(currentBoard, totalPages, 10, 'desc');
        };
        lastPageItem.appendChild(lastPageLink);
        paginationUl.appendChild(lastPageItem);
    }
}

function storeCurrentPage(page) {
    localStorage.setItem('currentPage', page);
}

function restorePage() {
    const savedPage = localStorage.getItem('currentPage');
    return savedPage ? parseInt(savedPage, 10) : 1; // 기본값으로 1 반환
}
