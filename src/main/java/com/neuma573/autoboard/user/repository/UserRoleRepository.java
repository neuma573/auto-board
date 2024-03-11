package com.neuma573.autoboard.user.repository;

import com.neuma573.autoboard.user.model.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Set;

public interface UserRoleRepository extends JpaRepository<UserRole,Long>, RevisionRepository<UserRole, Long, Long> {

    Set<UserRole> findByUserId(Long user);
}
