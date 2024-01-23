
async function initializePost() {
    showSpinner();
    const postId = localStorage.getItem("dest");
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
            hideSpinner();
            throw new Error(data.message || "Failed to load post");
        }
    } catch (error) {
        hideSpinner();
        console.error("Error fetching post:", error);
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
    hideSpinner();
}

document.addEventListener('DOMContentLoaded', initializePost);
