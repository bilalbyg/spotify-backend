package com.spotifyclone.auth.repository;

import com.spotifyclone.auth.model.RefreshToken;
import com.spotifyclone.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUserAndRevokedAtIsNull(User user);
}
