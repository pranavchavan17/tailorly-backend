package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.request.RegisterRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.UserResponse;
import com.tailorly.tailorly_backend.exception.ResourceAlreadyExistsException;
import com.tailorly.tailorly_backend.model.User;
import com.tailorly.tailorly_backend.repository.UserRepository;
import com.tailorly.tailorly_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<?> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        UserResponse userResponse = UserResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .emailVerified(savedUser.getEmailVerified())
                .build();

        return ApiResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(userResponse)   // <-- Return UserResponse instead of User
                .build();
    }
}
