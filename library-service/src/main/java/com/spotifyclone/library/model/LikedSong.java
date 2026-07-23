package com.spotifyclone.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("liked_songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedSong {

    @PrimaryKey
    private LikedSongKey key;

    @Column("liked_at")
    private Instant likedAt;
}