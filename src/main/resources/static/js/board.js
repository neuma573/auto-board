let currentBoard = -1;

document.addEventListener('DOMContentLoaded', function() {
    verifyToken(localStorage.getItem("accessToken")).then(() => {
        if(localStorage.getItem("accessToken") === null) {
            document.getElementById('writeButton').style.display = 'none';
        }
    })


    const savedBoardId = localStorage.getItem('currentBoardId');
    if (savedBoardId) {
        currentBoard = parseInt(savedBoardId, 10);
    }
    fetchBoards().then(() => {
        setupWriteButtonLink();
    });
});

async function fetchBoards() {
    showSpinner();
    await checkAndRefreshToken();
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
            await fetchPosts(currentBoard, 1, 10, 'desc');


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
        option.textContent = `${board.boardName} (${board.postCount})`;
        select.appendChild(option);
        if (board.public && firstPublicBoardId === null) {
            firstPublicBoardId = board.id;
        }
    });

    if (localStorage.getItem('currentBoardId') === null) {
        select.value = firstPublicBoardId;
        currentBoard = firstPublicBoardId;
        localStorage.setItem('currentBoardId', currentBoard);
        document.getElementById('writeButton').href = `/write?boardId=${currentBoard}`;
    } else {
        select.value = localStorage.getItem('currentBoardId');
        document.getElementById('writeButton').href = `/write?boardId=${currentBoard}`;
    }
}

function setupWriteButtonLink() {
    const select = document.getElementById('boardSelect');
    select.removeEventListener('change', handleBoardSelectChange);
    select.addEventListener('change', handleBoardSelectChange);
}

function handleBoardSelectChange() {
    const selectedBoardId = this.value;
    currentBoard = selectedBoardId;
    localStorage.setItem('currentBoardId', selectedBoardId);
    const writeButton = document.getElementById('writeButton');
    writeButton.href = `/write?boardId=${selectedBoardId}`;
    fetchPosts(selectedBoardId, 1, 10, 'desc');
}

async function fetchPosts(boardId, page, size, order) {
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

            const totalRecords = localStorage.getItem(boardId); // 백엔드로부터 받은 총 페이지 수
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

    posts.forEach((post, index) => {
        const row = tableBody.insertRow();
        const numberCell = row.insertCell(0);
        const titleCell = row.insertCell(1);
        const authorCell = row.insertCell(2);
        const dateCell = row.insertCell(3);
        const viewsCell = row.insertCell(4);

        numberCell.textContent = index + 1;

        const titleLink = document.createElement('a');
        titleLink.href = '#';
        titleLink.textContent = post.title;
        titleLink.onclick = function() { clickPost(post.id); };
        titleCell.appendChild(titleLink);

        authorCell.textContent = post.userResponse.name;
        dateCell.textContent = getFormattedCreatedAt(post.createdAt);
        viewsCell.textContent = post.views;
    });
}

function updateMobilePostsList(posts) {
    const listGroup = document.querySelector('.list-group');
    listGroup.innerHTML = '';
    posts.forEach(post => {
        const listItem = document.createElement('div');
        listItem.className = 'list-group-item';

        const postTitleLink = document.createElement('a');
        postTitleLink.href = '#';
        postTitleLink.className = 'post-title';



        if(post.title.length > 30) {
            postTitleLink.textContent = post.title.substring(0, 30) + '...';
        } else {
            postTitleLink.textContent = post.title;
        }



        postTitleLink.onclick = function() { clickPost(post.id); };
        listItem.appendChild(postTitleLink);

        const postDetails = document.createElement('div');
        postDetails.className = 'post-details';

        const authorSpan = document.createElement('span');
        authorSpan.className = 'author';
        authorSpan.textContent = post.userResponse.name;
        postDetails.appendChild(authorSpan);

        const statsSpan = document.createElement('span');
        statsSpan.className = 'stats';
        statsSpan.textContent = `작성일: ${getFormattedCreatedAt(post.createdAt)} | 조회수: ${post.views}`;
        postDetails.appendChild(statsSpan);

        listItem.appendChild(postDetails);
        listGroup.appendChild(listItem);
    });
}

function clickPost(postId) {
    localStorage.setItem("dest", postId);
    location.href = `/post?postId=${localStorage.getItem("dest")}`;
}

function createPaginationButtons(totalPages, currentPage) {
    const paginationUl = document.querySelector('.pagination');
    paginationUl.innerHTML = ''; // 기존 페이징 버튼 삭제

    // 페이지 버튼 생성
    for (let i = 1; i <= totalPages; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = `page-item ${i === currentPage ? 'active' : ''}`;
        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = '#';
        pageLink.textContent = i;
        pageLink.onclick = function() { fetchPosts(currentBoard, i, 10, 'desc'); };

        pageItem.appendChild(pageLink);
        paginationUl.appendChild(pageItem);
    }
}