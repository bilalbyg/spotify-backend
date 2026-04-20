package com.spotifyclone.catalog.controller;

import com.spotifyclone.catalog.dto.UserFollowsArtistRequest;
import com.spotifyclone.catalog.dto.UserFollowsArtistResponse;
import com.spotifyclone.catalog.service.UserFollowsArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/follows/artists")
@RequiredArgsConstructor
public class UserFollowsArtistController {

    private final UserFollowsArtistService userFollowsArtistService;

    @PostMapping
    public ResponseEntity<UserFollowsArtistResponse> followArtist(@RequestBody UserFollowsArtistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userFollowsArtistService.followArtist(request));
    }

    @DeleteMapping("/{userId}/{artistId}")
    public ResponseEntity<Void> unfollowArtist(@PathVariable UUID userId, @PathVariable UUID artistId) {
        userFollowsArtistService.unfollowArtist(userId, artistId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserFollowsArtistResponse>> getFollowedArtists(@PathVariable UUID userId) {
        return ResponseEntity.ok(userFollowsArtistService.getFollowedArtists(userId));
    }
}
