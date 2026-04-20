package com.spotifyclone.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserFollowsUserResponse(
        UUID id,
        UUID followerId,
        UUID followedId,
        LocalDateTime followedAt
) {}
