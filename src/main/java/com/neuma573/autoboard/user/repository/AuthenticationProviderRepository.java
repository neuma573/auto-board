package com.neuma573.autoboard.user.repository;

import com.neuma573.autoboard.user.model.entity.AuthenticationProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationProviderRepository extends JpaRepository<AuthenticationProvider, Long> {

    Optional<AuthenticationProvider> findByProviderId(String providerId);

}
