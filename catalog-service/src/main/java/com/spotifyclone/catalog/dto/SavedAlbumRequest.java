package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record SavedAlbumRequest(
        UUID userId,
        UUID albumId
) {}
