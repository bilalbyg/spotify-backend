package com.spotifyclone.auth.service;

import com.spotifyclone.auth.model.RefreshToken;
import com.spotifyclone.auth.model.User;
import com.spotifyclone.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Override
    public String createRefreshToken(User user) {
        String token = jwtService.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(jwtService.extractExpirationAsInstant(token))
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!jwtService.isRefreshToken(token)) {
            throw new RuntimeException("This is not a refresh token");
        }

        if (refreshToken.isExpired()) {
            throw new RuntimeException("Refresh token expired");
        }

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (!jwtService.isTokenValid(token, refreshToken.getUser())) {
            throw new RuntimeException("Invalid refresh token");
        }
        return refreshToken;
    }

    public String rotateRefreshToken(String oldToken) {
        RefreshToken currentRefreshToken = validateRefreshToken(oldToken);

        User user = currentRefreshToken.getUser();
        String newToken = jwtService.generateRefreshToken(user);

        currentRefreshToken.setRevokedAt(Instant.now());
        currentRefreshToken.setReplacedByToken(newToken);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(newToken)
                .expiresAt(jwtService.extractExpirationAsInstant(newToken))
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(currentRefreshToken);
        refreshTokenRepository.save(newRefreshToken);

        return newToken;
    }

    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            return;
        }

        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }
}
