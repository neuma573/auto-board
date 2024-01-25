package com.neuma573.autoboard.comment.repository;

import com.neuma573.autoboard.comment.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
