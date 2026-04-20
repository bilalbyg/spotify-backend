package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record UserFollowsArtistRequest(
        UUID userId,
        UUID artistId
) {}
