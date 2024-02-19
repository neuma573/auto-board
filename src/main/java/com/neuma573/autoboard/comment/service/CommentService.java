package com.neuma573.autoboard.comment.service;

import com.neuma573.autoboard.comment.event.CommentEvent;
import com.neuma573.autoboard.comment.model.dto.CommentModifyRequest;
import com.neuma573.autoboard.comment.model.dto.CommentRequest;
import com.neuma573.autoboard.comment.model.dto.CommentResponse;
import com.neuma573.autoboard.comment.model.entity.Comment;
import com.neuma573.autoboard.comment.model.enums.CommentAction;
import com.neuma573.autoboard.comment.repository.CommentRepository;
import com.neuma573.autoboard.global.exception.CommentNotAccessibleException;
import com.neuma573.autoboard.global.exception.UserBlockedException;
import com.neuma573.autoboard.global.model.enums.Status;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.post.service.PostService;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final PostService postService;

    private final ApplicationEventPublisher applicationEventPublisher;

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
    public void modifyComment(CommentModifyRequest commentModifyRequest, Long userId) {
        Comment comment = getCommentById(commentModifyRequest.getCommentId());
        comment.setCurrentUser(userService.getUserById(userId));
        applicationEventPublisher.publishEvent(new CommentEvent(this, comment, CommentAction.UPDATE));
        modify(comment, commentModifyRequest);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);
        comment.setCurrentUser(userService.getUserById(userId));
        applicationEventPublisher.publishEvent(new CommentEvent(this, comment, CommentAction.DELETE));
        delete(comment);
    }

    private void delete(Comment comment) {
        comment.delete();
    }

    private void modify(Comment comment, CommentModifyRequest commentModifyRequest) {
        comment.setContent(commentModifyRequest.getContent());
    }

    @Transactional
    public Long findPostIdByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .map(Comment::getPost)
                .map(Post::getId)
                .orElse(null);
    }

    @Transactional
    public boolean isCommentAccessible(Long userId, Long commentId, CommentAction action) {
        Comment comment = getCommentById(commentId);
        User user = userService.getUserByIdSafely(userId);

        if (user != null && user.getStatus().equals(Status.BANNED.getStatus())) {
            throw new UserBlockedException(userId);
        }

        return switch (action) {
            case UPDATE -> !comment.isDeleted() && isCreatedBy(userId, comment);
            case DELETE -> !comment.isDeleted() && (user != null && userService.isAdmin(user)) || isCreatedBy(userId, comment);
            default -> false;
        };
    }

    @Transactional
    public boolean isCreatedBy(Long userId, Comment comment) {
        return Objects.equals(userId, comment.getCreatedBy().getId());
    }

}
