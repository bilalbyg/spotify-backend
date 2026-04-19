package com.spotifyclone.catalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "lyrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lyrics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false, unique = true)
    private Song song;
}
