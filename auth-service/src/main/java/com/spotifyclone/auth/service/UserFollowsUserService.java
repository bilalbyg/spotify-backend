package com.spotifyclone.auth.service;

import com.spotifyclone.auth.dto.UserFollowsUserRequest;
import com.spotifyclone.auth.dto.UserFollowsUserResponse;

import java.util.List;
import java.util.UUID;

public interface UserFollowsUserService {
    UserFollowsUserResponse followUser(UserFollowsUserRequest request);
    void unfollowUser(UUID followerId, UUID followedId);
    List<UserFollowsUserResponse> getFollowing(UUID followerId);
    List<UserFollowsUserResponse> getFollowers(UUID followedId);
}
