package com.neuma573.autoboard.security.repository;

import com.neuma573.autoboard.security.model.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, String> {

    Optional<VerificationToken> findByToken(String token);
}