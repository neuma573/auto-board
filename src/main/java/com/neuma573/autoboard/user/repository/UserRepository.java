package com.neuma573.autoboard.user.repository;

import com.neuma573.autoboard.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository, RevisionRepository<User, Long, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
