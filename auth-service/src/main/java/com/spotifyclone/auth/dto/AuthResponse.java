package com.spotifyclone.auth.dto;

public record AuthResponse(
        String token,
        String username,
        String email
) {}