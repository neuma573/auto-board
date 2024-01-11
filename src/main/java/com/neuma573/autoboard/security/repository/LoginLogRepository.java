package com.neuma573.autoboard.security.repository;

import com.neuma573.autoboard.security.model.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

}
