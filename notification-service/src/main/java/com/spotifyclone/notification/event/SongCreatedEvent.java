package com.spotifyclone.notification.event;

import java.util.UUID;

    public record SongCreatedEvent(
        UUID id,
        String title,
        String artist
) {
}