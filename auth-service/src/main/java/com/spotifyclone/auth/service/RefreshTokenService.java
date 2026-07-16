package com.spotifyclone.auth.service;

import com.spotifyclone.auth.model.RefreshToken;
import com.spotifyclone.auth.model.User;
import org.springframework.stereotype.Service;

public interface RefreshTokenService {
    String createRefreshToken(User   user);
    RefreshToken validateRefreshToken(String token);
    String rotateRefreshToken(String token);
}
