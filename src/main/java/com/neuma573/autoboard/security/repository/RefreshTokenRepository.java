package com.neuma573.autoboard.security.repository;

import com.neuma573.autoboard.security.model.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
