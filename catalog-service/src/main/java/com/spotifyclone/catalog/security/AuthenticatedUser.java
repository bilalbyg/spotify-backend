package com.spotifyclone.catalog.security;

import java.util.UUID;

/**
 * The authenticated user's identity, decoded from the JWT and set as the
 * Spring Security principal by {@link JwtAuthenticationFilter}. Carries the
 * user UUID (from the {@code uid} claim) so downstream controllers can source
 * the actor without trusting a request body.
 */
public record AuthenticatedUser(UUID userId, String email, String role) {
}
