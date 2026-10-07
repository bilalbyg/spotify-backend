package com.spotifyclone.auth.controller;

import com.spotifyclone.auth.dto.AuthResponse;
import com.spotifyclone.auth.dto.LoginRequest;
import com.spotifyclone.auth.dto.RefreshTokenRequest;
import com.spotifyclone.auth.dto.RegisterRequest;
import com.spotifyclone.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok().body(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", authentication != null && authentication.isAuthenticated());
        if (authentication != null) {
            result.put("name", authentication.getName());
            result.put("authorities", authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .toList());
        }
        return ResponseEntity.ok(result);
    }
}