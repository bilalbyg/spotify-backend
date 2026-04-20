package com.spotifyclone.auth.controller;

import com.spotifyclone.auth.dto.UserFollowsUserRequest;
import com.spotifyclone.auth.dto.UserFollowsUserResponse;
import com.spotifyclone.auth.service.UserFollowsUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/follows")
@RequiredArgsConstructor
public class UserFollowsUserController {

    private final UserFollowsUserService userFollowsUserService;

    @PostMapping
    public ResponseEntity<UserFollowsUserResponse> followUser(@RequestBody UserFollowsUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userFollowsUserService.followUser(request));
    }

    @DeleteMapping("/{followerId}/{followedId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable UUID followerId, @PathVariable UUID followedId) {
        userFollowsUserService.unfollowUser(followerId, followedId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserFollowsUserResponse>> getFollowing(@PathVariable UUID userId) {
        return ResponseEntity.ok(userFollowsUserService.getFollowing(userId));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserFollowsUserResponse>> getFollowers(@PathVariable UUID userId) {
        return ResponseEntity.ok(userFollowsUserService.getFollowers(userId));
    }
}
