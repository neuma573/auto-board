package com.neuma573.autoboard.comment.service;

import com.neuma573.autoboard.comment.model.dto.CommentModifyRequest;
import com.neuma573.autoboard.comment.model.dto.CommentRequest;
import com.neuma573.autoboard.comment.model.dto.CommentResponse;
import com.neuma573.autoboard.comment.model.entity.Comment;
import com.neuma573.autoboard.comment.repository.CommentRepository;
import com.neuma573.autoboard.global.exception.CommentNotAccessibleException;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final PostService postService;

    @Transactional
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new CommentNotAccessibleException("접근할 수 없는 댓글입니다."));
    }

    @Transactional
    public CommentResponse saveComment(CommentRequest commentRequest, Long userId) {

        Post parentPost = postService.getPostById(commentRequest.getPostId());
        User createdBy = userService.getUserById(userId);

        Comment parentComment = null;
        if (commentRequest.getParentId() != null) {
            parentComment = getCommentById(commentRequest.getParentId());
        }

        return CommentResponse.of(commentRepository.save(commentRequest.toEntity(parentPost, createdBy, parentComment)));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentList(Long postId, Pageable pageable, Long userId) {

        User user = userService.getUserByIdSafely(userId);

        Page<Comment> comments = (user != null && userService.isAdmin(user))
                ? commentRepository.findAllByPostId(postId, pageable)
                : commentRepository.findAllByPostIdAndIsDeletedFalse(postId, pageable);

        return comments.map(CommentResponse::of);
    }

    @Transactional
    public void modifyComment(CommentModifyRequest commentModifyRequest) {
        Comment comment = getCommentById(commentModifyRequest.getCommentId());
        modify(comment, commentModifyRequest);
    }

    @Transactional
    public void deleteComment(Long commentId) {
       delete(getCommentById(commentId));
    }

    private void delete(Comment comment) {
        comment.delete();
    }

    private void modify(Comment comment, CommentModifyRequest commentModifyRequest) {
        comment.setContent(commentModifyRequest.getContent());
    }

}
