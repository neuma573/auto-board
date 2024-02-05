package com.neuma573.autoboard.automate.repository;

import com.neuma573.autoboard.automate.model.entity.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WhitelistRepository extends JpaRepository<Whitelist, Long> {

    List<Whitelist> findByAppName(String appName);

}
