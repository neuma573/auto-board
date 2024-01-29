package com.neuma573.autoboard.comment.repository;

import com.neuma573.autoboard.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    Page<Comment> findAllByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);
}
