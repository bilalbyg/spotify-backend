package com.spotifyclone.auth.dto;

import java.util.UUID;

public record UserFollowsUserRequest(
        UUID followerId,
        UUID followedId
) {}
