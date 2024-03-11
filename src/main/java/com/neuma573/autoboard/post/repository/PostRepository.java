package com.neuma573.autoboard.post.repository;

import com.neuma573.autoboard.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface PostRepository extends JpaRepository<Post, Long>, RevisionRepository<Post, Long, Long> {

    Page<Post> findAllByBoardId(Long boardId, Pageable pageable);

    Page<Post> findAllByBoardIdAndIsDeletedFalse(Long boardId, Pageable pageable);
}
