package com.neuma573.autoboard.comment.repository;

import com.neuma573.autoboard.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;


public interface CommentRepository extends JpaRepository<Comment, Long>, RevisionRepository<Comment, Long, Long> {

    Page<Comment> findAllByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);

    Page<Comment> findAllByPostIdAndIsDeletedFalseAndParentCommentIsNull(Long postId, Pageable pageable);

    Page<Comment> findByParentCommentIdAndIsDeletedFalseAndIdGreaterThanOrderByIdAsc(Long parentCommentId, Long lastCommentId, Pageable pageable);

    long countByParentCommentIdAndIsDeletedFalseAndIdGreaterThan(Long parentCommentId, Long lastCommentId);

    Page<Comment> findByParentCommentIdAndIsDeletedFalseOrderByIdAsc(Long parentCommentId, Pageable pageable);

}
