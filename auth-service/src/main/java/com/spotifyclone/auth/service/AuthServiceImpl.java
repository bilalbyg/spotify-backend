package com.spotifyclone.auth.service;

import com.spotifyclone.auth.dto.AuthResponse;
import com.spotifyclone.auth.dto.LoginRequest;
import com.spotifyclone.auth.dto.RefreshTokenRequest;
import com.spotifyclone.auth.dto.RegisterRequest;
import com.spotifyclone.auth.model.Role;
import com.spotifyclone.auth.model.User;
import com.spotifyclone.auth.repository.UserRepository;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda!");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış!");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, user.getActualUsername(), user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, user.getActualUsername(), user.getEmail());
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String oldRefreshToken = request.refreshToken();
        String newRefreshToken = refreshTokenService.rotateRefreshToken(oldRefreshToken);

        String email = jwtService.extractUserName(newRefreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(
          newAccessToken,
          newRefreshToken,
          user.getActualUsername(), user.getEmail()
        );
    }

    @Override
    public void logout(RefreshTokenRequest request) {

    }
}
