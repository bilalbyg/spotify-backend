package com.spotifyclone.catalog.dto;

import java.util.UUID;

public record SavedPlaylistRequest(
        UUID userId,
        UUID playlistId
) {}
