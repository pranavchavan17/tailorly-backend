package com.tailorly.tailorly_backend.controller;

import com.tailorly.tailorly_backend.model.User;
import com.tailorly.tailorly_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;

    @GetMapping("/api/test")
    public String test() {
        return "Tailorly Backend Running Successfully";
    }

    @GetMapping("/api/test/user")
    public String createUser() {

        User user = User.builder()
                .name("Pranav")
                .email("pranav@test.com")
                .password("123456")
                .build();

        userRepository.save(user);

        return "User Saved Successfully";
    }
}