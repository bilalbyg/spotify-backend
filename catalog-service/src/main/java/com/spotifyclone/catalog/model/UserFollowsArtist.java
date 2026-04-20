package com.spotifyclone.catalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_follows_artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollowsArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId; // Referencing auth-service User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Builder.Default
    private LocalDateTime followedAt = LocalDateTime.now();
}
