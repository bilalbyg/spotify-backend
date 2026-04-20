package com.spotifyclone.catalog.service;

import com.spotifyclone.catalog.dto.UserFollowsArtistRequest;
import com.spotifyclone.catalog.dto.UserFollowsArtistResponse;
import com.spotifyclone.catalog.model.Artist;
import com.spotifyclone.catalog.model.UserFollowsArtist;
import com.spotifyclone.catalog.repository.ArtistRepository;
import com.spotifyclone.catalog.repository.UserFollowsArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFollowsArtistServiceImpl implements UserFollowsArtistService {

    private final UserFollowsArtistRepository userFollowsArtistRepository;
    private final ArtistRepository artistRepository;

    @Override
    @Transactional
    public UserFollowsArtistResponse followArtist(UserFollowsArtistRequest request) {
        if (userFollowsArtistRepository.existsByUserIdAndArtistId(request.userId(), request.artistId())) {
            throw new RuntimeException("Artist already followed");
        }

        Artist artist = artistRepository.findById(request.artistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        UserFollowsArtist followedArtist = UserFollowsArtist.builder()
                .userId(request.userId())
                .artist(artist)
                .followedAt(LocalDateTime.now())
                .build();

        followedArtist = userFollowsArtistRepository.save(followedArtist);
        return mapToResponse(followedArtist);
    }

    @Override
    @Transactional
    public void unfollowArtist(UUID userId, UUID artistId) {
        UserFollowsArtist followedArtist = userFollowsArtistRepository.findByUserIdAndArtistId(userId, artistId)
                .orElseThrow(() -> new RuntimeException("Followed artist not found"));
        userFollowsArtistRepository.delete(followedArtist);
    }

    @Override
    public List<UserFollowsArtistResponse> getFollowedArtists(UUID userId) {
        return userFollowsArtistRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserFollowsArtistResponse mapToResponse(UserFollowsArtist followedArtist) {
        return new UserFollowsArtistResponse(
                followedArtist.getId(),
                followedArtist.getUserId(),
                followedArtist.getArtist().getId(),
                followedArtist.getFollowedAt()
        );
    }
}
