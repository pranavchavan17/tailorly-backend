package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.request.ChangePasswordRequest;
import com.tailorly.tailorly_backend.dto.request.UpdateProfileRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.UserResponse;
import com.tailorly.tailorly_backend.exception.InvalidCredentialsException;
import com.tailorly.tailorly_backend.mapper.UserMapper;
import com.tailorly.tailorly_backend.model.User;
import com.tailorly.tailorly_backend.repository.UserRepository;
import com.tailorly.tailorly_backend.service.CurrentUserService;
import com.tailorly.tailorly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    @Override
    public ApiResponse<UserResponse> getCurrentUser() {

        User user = currentUserService.getCurrentUser();

        UserResponse response = userMapper.toUserResponse(user);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<UserResponse> updateProfile(UpdateProfileRequest request) {

        User user = currentUserService.getCurrentUser();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User updatedUser = userRepository.save(user);

        UserResponse response = userMapper.toUserResponse(updatedUser);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<Void> changePassword(ChangePasswordRequest request) {

        User user = currentUserService.getCurrentUser();

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Prevent using the same password again
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the current password");
        }

        // Encrypt and save new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Password changed successfully")
                .data(null)
                .build();
    }
}