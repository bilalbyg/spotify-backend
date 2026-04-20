package com.spotifyclone.catalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saved_playlists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedPlaylist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId; // Referencing auth-service User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @Builder.Default
    private LocalDateTime savedAt = LocalDateTime.now();
}
