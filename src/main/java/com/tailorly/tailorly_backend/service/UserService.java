package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.request.ChangePasswordRequest;
import com.tailorly.tailorly_backend.dto.request.UpdateProfileRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.UserResponse;

public interface UserService {

    ApiResponse<UserResponse> getCurrentUser();
    ApiResponse<UserResponse> updateProfile(UpdateProfileRequest request);
    ApiResponse<Void> changePassword(ChangePasswordRequest request);
}