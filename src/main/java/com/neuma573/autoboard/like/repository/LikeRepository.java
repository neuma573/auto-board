package com.neuma573.autoboard.like.repository;

import com.neuma573.autoboard.like.model.entity.Like;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByPostId(Long id);

    Optional<Like> findByUserAndPost(User user, Post post);
}
