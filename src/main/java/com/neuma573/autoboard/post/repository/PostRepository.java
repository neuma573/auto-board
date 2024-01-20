package com.neuma573.autoboard.post.repository;

import com.neuma573.autoboard.post.model.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
}
