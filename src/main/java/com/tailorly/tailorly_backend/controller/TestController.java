package com.tailorly.tailorly_backend.controller;

import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.UserResponse;
import com.tailorly.tailorly_backend.mapper.UserMapper;
import com.tailorly.tailorly_backend.model.User;
import com.tailorly.tailorly_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/api/test")
    public String test() {
        return "Tailorly Backend Running Successfully";
    }

    @GetMapping("/api/test/user")
    public ApiResponse<UserResponse> createUser() {

        User user = User.builder()
                .firstName("Pranav")
                .lastName("Chavan")
                .email("pranav@test.com")
                .password("123456")
                .build();

        User savedUser = userRepository.save(user);

        UserResponse response = userMapper.toUserResponse(savedUser);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User saved successfully")
                .data(response)
                .build();
    }
}