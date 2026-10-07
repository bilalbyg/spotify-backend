package com.spotifyclone.auth.controller;

import com.spotifyclone.auth.dto.CreateUserRequest;
import com.spotifyclone.auth.dto.UpdateUserRequest;
import com.spotifyclone.auth.dto.UserCountResponse;
import com.spotifyclone.auth.dto.UserResponse;
import com.spotifyclone.auth.security.annotation.AdminOnly;
import com.spotifyclone.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/users")
@RequiredArgsConstructor
@AdminOnly
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/count")
    public ResponseEntity<UserCountResponse> getUserCount() {
        return ResponseEntity.ok(new UserCountResponse(userService.getUserCount()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
