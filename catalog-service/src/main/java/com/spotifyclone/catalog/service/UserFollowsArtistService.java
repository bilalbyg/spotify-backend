package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.UserFollowsArtistRequest;
import com.spotifyclone.catalog.dto.UserFollowsArtistResponse;

import java.util.List;
import java.util.UUID;

public interface UserFollowsArtistService {
    UserFollowsArtistResponse followArtist(UserFollowsArtistRequest request);
    void unfollowArtist(UUID userId, UUID artistId);
    List<UserFollowsArtistResponse> getFollowedArtists(UUID userId);
}
