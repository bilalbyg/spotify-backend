package com.spotifyclone.auth.service;

import com.spotifyclone.auth.dto.CreateUserRequest;
import com.spotifyclone.auth.dto.UpdateUserRequest;
import com.spotifyclone.auth.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);

    long getUserCount();
}
