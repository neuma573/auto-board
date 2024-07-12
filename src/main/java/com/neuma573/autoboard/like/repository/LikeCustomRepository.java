package com.neuma573.autoboard.like.repository;

import com.neuma573.autoboard.like.model.entity.Like;
import com.neuma573.autoboard.post.model.entity.Post;
import com.neuma573.autoboard.user.model.entity.User;

import java.util.Optional;

public interface LikeCustomRepository {

    Optional<Like> findByCreatedByAndPostForUpdate(User user, Post post);

    Long countByPostId(Long postId);
}
