package com.spotifyclone.auth.dto;

import com.spotifyclone.auth.model.Role;
import com.spotifyclone.auth.model.SubscriptionType;

public record UpdateUserRequest(
        String username,
        Role role,
        SubscriptionType subscriptionType,
        boolean emailVerified
) {
}
