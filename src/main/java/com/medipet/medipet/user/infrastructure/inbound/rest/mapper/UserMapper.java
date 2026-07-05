package com.medipet.medipet.user.infrastructure.inbound.rest.mapper;

import com.medipet.medipet.user.infrastructure.inbound.rest.dto.response.UserResponse;
import com.medipet.medipet.user.domain.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId().getValue().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.isActive(),
                user.getPhone(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getAvatar(),
                user.getLastLoginAt()
        );
    }
}
