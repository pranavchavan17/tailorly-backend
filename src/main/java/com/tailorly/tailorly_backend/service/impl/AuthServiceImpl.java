package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.request.LoginRequest;
import com.tailorly.tailorly_backend.dto.request.RegisterRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.LoginResponse;
import com.tailorly.tailorly_backend.dto.response.UserResponse;
import com.tailorly.tailorly_backend.exception.InvalidCredentialsException;
import com.tailorly.tailorly_backend.exception.ResourceAlreadyExistsException;
import com.tailorly.tailorly_backend.mapper.UserMapper;
import com.tailorly.tailorly_backend.model.User;
import com.tailorly.tailorly_backend.repository.UserRepository;
import com.tailorly.tailorly_backend.service.AuthService;
import com.tailorly.tailorly_backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

        UserResponse userResponse = userMapper.toUserResponse(savedUser);

        return ApiResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(userResponse)
                .build();
    }

    @Override
    public ApiResponse<?> login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        UserResponse userResponse = userMapper.toUserResponse(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .user(userResponse)
                .build();

        return ApiResponse.builder()
                .success(true)
                .message("Login successful")
                .data(loginResponse)
                .build();
    }
}