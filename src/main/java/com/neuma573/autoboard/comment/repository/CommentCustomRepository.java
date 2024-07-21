package com.neuma573.autoboard.comment.repository;

import com.neuma573.autoboard.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentCustomRepository {
    Page<Comment> findParentCommentsByPostIdIncludingDeletedWithReplies(Long postId, Pageable pageable);

}
