package com.spotifyclone.auth.dto;

import com.spotifyclone.auth.model.Gender;
import com.spotifyclone.auth.model.Role;
import com.spotifyclone.auth.model.SubscriptionType;

import java.time.LocalDate;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        LocalDate dateOfBirth,
        Gender gender,
        Role role,
        SubscriptionType subscriptionType,
        boolean emailVerified
) {
}
