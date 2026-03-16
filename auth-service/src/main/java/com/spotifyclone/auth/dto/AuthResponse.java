package com.spotifyclone.auth.dto;

// Kullanıcı başarıyla giriş yaptığında ona Token'ını ve temel bilgilerini döneceğiz
public record AuthResponse(
        String token,
        String username,
        String email
) {}