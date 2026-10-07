package com.spotifyclone.auth.service;

import com.spotifyclone.auth.dto.CreateUserRequest;
import com.spotifyclone.auth.dto.UpdateUserRequest;
import com.spotifyclone.auth.dto.UserResponse;
import com.spotifyclone.auth.model.User;
import com.spotifyclone.auth.repository.RefreshTokenRepository;
import com.spotifyclone.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
        return toResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda!");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Bu kullanıcı adı zaten alınmış!");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .subscriptionType(request.subscriptionType())
                .emailVerified(request.emailVerified())
                .build();

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        if (request.username() != null && !request.username().isBlank()) {
            user.setUsername(request.username());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.subscriptionType() != null) {
            user.setSubscriptionType(request.subscriptionType());
        }
        user.setEmailVerified(request.emailVerified());

        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && user.getEmail().equals(authentication.getName())) {
            throw new RuntimeException("Kendi kullanıcı hesabınızı silemezsiniz!");
        }

        refreshTokenRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getActualUsername(),
                user.getRole(),
                user.getSubscriptionType(),
                user.isEmailVerified()
        );
    }
}
