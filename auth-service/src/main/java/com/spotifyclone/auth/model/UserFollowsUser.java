package com.spotifyclone.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_follows_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollowsUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    @Builder.Default
    private LocalDateTime followedAt = LocalDateTime.now();
}
