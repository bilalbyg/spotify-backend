package com.spotifyclone.auth.dto;

import com.spotifyclone.auth.model.Role;
import com.spotifyclone.auth.model.SubscriptionType;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String username,
        Role role,
        SubscriptionType subscriptionType,
        boolean emailVerified
) {
}
