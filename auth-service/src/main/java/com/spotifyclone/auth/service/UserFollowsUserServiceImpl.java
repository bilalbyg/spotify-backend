package com.spotifyclone.auth.service;

import com.spotifyclone.auth.dto.UserFollowsUserRequest;
import com.spotifyclone.auth.dto.UserFollowsUserResponse;
import com.spotifyclone.auth.model.User;
import com.spotifyclone.auth.model.UserFollowsUser;
import com.spotifyclone.auth.repository.UserFollowsUserRepository;
import com.spotifyclone.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFollowsUserServiceImpl implements UserFollowsUserService {

    private final UserFollowsUserRepository userFollowsUserRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserFollowsUserResponse followUser(UserFollowsUserRequest request) {
        if (request.followerId().equals(request.followedId())) {
            throw new RuntimeException("Kullanıcı kendi kendini takip edemez");
        }

        if (userFollowsUserRepository.existsByFollowerIdAndFollowedId(request.followerId(), request.followedId())) {
            throw new RuntimeException("Zaten takip ediliyor");
        }

        User follower = userRepository.findById(request.followerId())
                .orElseThrow(() -> new RuntimeException("Takip eden kullanıcı bulunamadı"));
        User followed = userRepository.findById(request.followedId())
                .orElseThrow(() -> new RuntimeException("Takip edilecek kullanıcı bulunamadı"));

        UserFollowsUser follow = UserFollowsUser.builder()
                .follower(follower)
                .followed(followed)
                .followedAt(LocalDateTime.now())
                .build();

        follow = userFollowsUserRepository.save(follow);
        return mapToResponse(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(UUID followerId, UUID followedId) {
        UserFollowsUser follow = userFollowsUserRepository.findByFollowerIdAndFollowedId(followerId, followedId)
                .orElseThrow(() -> new RuntimeException("Takip ilişkisi bulunamadı"));
        userFollowsUserRepository.delete(follow);
    }

    @Override
    public List<UserFollowsUserResponse> getFollowing(UUID followerId) {
        return userFollowsUserRepository.findByFollowerId(followerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserFollowsUserResponse> getFollowers(UUID followedId) {
        return userFollowsUserRepository.findByFollowedId(followedId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserFollowsUserResponse mapToResponse(UserFollowsUser follow) {
        return new UserFollowsUserResponse(
                follow.getId(),
                follow.getFollower().getId(),
                follow.getFollowed().getId(),
                follow.getFollowedAt()
        );
    }
}
