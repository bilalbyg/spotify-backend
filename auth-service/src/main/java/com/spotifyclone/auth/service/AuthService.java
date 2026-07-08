package com.spotifyclone.auth.service;

import com.spotifyclone.auth.dto.AuthResponse;
import com.spotifyclone.auth.dto.LoginRequest;
import com.spotifyclone.auth.dto.RefreshTokenRequest;
import com.spotifyclone.auth.dto.RegisterRequest;
import com.spotifyclone.auth.model.Role;
import com.spotifyclone.auth.model.User;
import com.spotifyclone.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
