package com.tailorly.tailorly_backend.controller;

import com.tailorly.tailorly_backend.dto.request.ChangePasswordRequest;
import com.tailorly.tailorly_backend.dto.request.UpdateProfileRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.UserResponse;
import com.tailorly.tailorly_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PutMapping("/profile")
    public ApiResponse<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        return userService.updateProfile(request);
    }
    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        return userService.changePassword(request);
    }
}