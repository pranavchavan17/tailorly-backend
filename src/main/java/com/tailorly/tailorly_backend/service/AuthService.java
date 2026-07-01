package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.request.RegisterRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;

public interface AuthService {

    ApiResponse<?> register(RegisterRequest request);
}