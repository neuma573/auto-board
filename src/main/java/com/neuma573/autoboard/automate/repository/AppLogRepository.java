package com.neuma573.autoboard.automate.repository;

import com.neuma573.autoboard.automate.model.entity.AppLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppLogRepository extends JpaRepository<AppLog, Long> {
}
