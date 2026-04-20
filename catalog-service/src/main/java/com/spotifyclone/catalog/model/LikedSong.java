package com.spotifyclone.catalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "liked_songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId; // Referencing auth-service User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();
}
