package com.neuma573.autoboard.comment.service;

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
import com.neuma573.autoboard.user.model.dto.UserResponse;
import com.neuma573.autoboard.user.model.entity.User;
import com.neuma573.autoboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        Comment parentComment = validateParentComment(commentRequest.getParentId());

        return CommentResponse.of(commentRepository.save(commentRequest.toEntity(parentPost, createdBy, parentComment)));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentList(Long postId, Pageable pageable, Long userId) {

        User user = userService.getUserByIdSafely(userId);
        boolean isAdmin = user != null && userService.isAdmin(user);

        Page<Comment> comments = isAdmin
                ? commentRepository.findAllByPostIdAndParentCommentIsNull(postId, pageable)
                : commentRepository.findParentCommentsByPostIdIncludingDeletedWithReplies(postId, pageable);

        return comments.map(comment -> {
            CommentResponse response = CommentResponse.of(comment);
            if (comment.isDeleted() && !isAdmin) {
                return CommentResponse.builder()
                        .id(response.getId())
                        .content("[삭제된 댓글입니다]")
                        .createdBy(UserResponse.builder()
                                .name(" ")
                                .build())
                        .isDeleted(true)
                        .createdAt(response.getCreatedAt())
                        .childCount(response.getChildCount())
                        .build();
            }
            return response;
        });
    }

    @Transactional
    public void modifyComment(CommentModifyRequest commentModifyRequest, Long userId) {
        Comment comment = getCommentById(commentModifyRequest.getCommentId());
        comment.setCurrentUser(userService.getUserById(userId));
        modify(comment, commentModifyRequest);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);
        comment.setCurrentUser(userService.getUserById(userId));
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

    @Transactional
    public Comment validateParentComment(Long parentId) {
        if (parentId == null) {
            return null;
        }

        Comment parentComment = getCommentById(parentId);
        if (parentComment.getParentComment() != null) {
            throw new IllegalArgumentException("대댓글에 대댓글을 작성할 수 없습니다");
        }

        return parentComment;
    }

    @Transactional
    public List<CommentResponse> getReplies(Long parentCommentId, Long lastCommentId, int size) {
        List<Comment> replies;

        if (lastCommentId == 0) {
            replies = commentRepository.findByParentCommentIdAndIsDeletedFalseOrderByIdAsc(parentCommentId, Pageable.ofSize(10)).getContent();
        } else {
            replies = commentRepository.findByParentCommentIdAndIsDeletedFalseAndIdGreaterThanOrderByIdAsc(parentCommentId, lastCommentId, PageRequest.of(0, size)).getContent();
        }
        return replies.stream().map(CommentResponse::of).collect(Collectors.toList());
    }

    public boolean hasMoreReplies(Long parentCommentId, Long lastCommentId) {
        long remainingReplies = commentRepository.countByParentCommentIdAndIsDeletedFalseAndIdGreaterThan(parentCommentId, lastCommentId);
        return remainingReplies > 0;
    }

}
