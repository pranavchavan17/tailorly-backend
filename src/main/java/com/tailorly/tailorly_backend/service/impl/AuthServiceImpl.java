package com.tailorly.tailorly_backend.service.impl;

import com.tailorly.tailorly_backend.dto.request.RegisterRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public ApiResponse<?> register(RegisterRequest request) {

        return ApiResponse.builder()
                .success(true)
                .message("Registration service working")
                .data(null)
                .build();
    }
}
