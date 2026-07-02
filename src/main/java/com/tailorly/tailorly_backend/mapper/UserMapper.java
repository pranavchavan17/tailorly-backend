package com.tailorly.tailorly_backend.mapper;

import com.tailorly.tailorly_backend.dto.response.UserResponse;
import com.tailorly.tailorly_backend.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);
}
