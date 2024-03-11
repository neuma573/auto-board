package com.neuma573.autoboard.comment.repository;

import com.neuma573.autoboard.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, RevisionRepository<Comment, Long, Long> {

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    Page<Comment> findAllByPostIdAndIsDeletedFalse(Long postId, Pageable pageable);
}
