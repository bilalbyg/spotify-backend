package com.spotifyclone.auth.dto;

import com.spotifyclone.auth.model.Gender;

import java.time.LocalDate;

public record RegisterRequest(
        String username,
        String email,
        String password,
        LocalDate dateOfBirth,
        Gender gender
) {}