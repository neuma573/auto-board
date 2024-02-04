package com.neuma573.autoboard.post.repository;

import com.neuma573.autoboard.post.model.entity.PostHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHistoryRepository extends JpaRepository<PostHistory, Long> {
}
