package com.neuma573.autoboard.comment.repository;

import com.neuma573.autoboard.comment.model.entity.CommentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentHistoryRepository extends JpaRepository<CommentHistory, Long> {
}
