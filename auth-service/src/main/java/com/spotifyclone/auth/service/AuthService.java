package com.spotifyclone.auth.service;

import com.spotifyclone.auth.dto.AuthResponse;
import com.spotifyclone.auth.dto.LoginRequest;
import com.spotifyclone.auth.dto.RegisterRequest;
import com.spotifyclone.auth.model.Role;
import com.spotifyclone.auth.model.User;
import com.spotifyclone.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

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
                .role(Role.USER)
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .build();

        userRepository.save(user);

        // JWT üretimini kullanıcı nesnesi üzerinden yaparak rol bilgisini de token'a ekliyoruz.
        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken, user.getActualUsername(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        // JWT üretimini kullanıcı nesnesi üzerinden yaparak rol bilgisini de token'a ekliyoruz.
        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken, user.getActualUsername(), user.getEmail());
    }
}
