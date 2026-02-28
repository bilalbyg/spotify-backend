package com.spotifyclone.catalog.event;

import java.util.UUID;

// Kafka'ya fırlatacağımız olayın (event) taşıyacağı veriler
public record SongCreatedEvent(
        UUID id,
        String title,
        String artist
) {
}