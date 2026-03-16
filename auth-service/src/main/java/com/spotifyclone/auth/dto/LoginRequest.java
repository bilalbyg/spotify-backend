package com.spotifyclone.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}