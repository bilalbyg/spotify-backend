package com.spotifyclone.auth.dto;

public record RegisterRequest(
        String username,
        String email,
        String password
) {}