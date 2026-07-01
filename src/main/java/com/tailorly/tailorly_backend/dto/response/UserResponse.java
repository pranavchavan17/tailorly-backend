package com.tailorly.tailorly_backend.dto.response;

import com.tailorly.tailorly_backend.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Boolean emailVerified;
}
