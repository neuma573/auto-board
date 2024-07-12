package com.neuma573.autoboard.like.repository;

import com.neuma573.autoboard.like.model.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeCustomRepository, RevisionRepository<Like, Long, Long> {
}
