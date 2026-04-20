package com.spotifyclone.library.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    private UUID userId;

    @Column(nullable = false)
    private UUID songId;

    @CreationTimestamp
    private LocalDateTime likedAt;
}
