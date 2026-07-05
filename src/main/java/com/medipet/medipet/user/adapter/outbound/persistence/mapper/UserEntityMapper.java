package com.medipet.medipet.user.adapter.outbound.persistence.mapper;

import com.medipet.medipet.user.adapter.outbound.persistence.UserEntity;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.valueobject.UserId;

public class UserEntityMapper {

    private UserEntityMapper() {
    }

    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId().getValue());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setRole(user.getRole().name());
        entity.setActive(user.isActive());
        entity.setPhone(user.getPhone());
        entity.setAddress(user.getAddress());
        entity.setDateOfBirth(user.getDateOfBirth());
        entity.setAvatar(user.getAvatar());
        entity.setLastLoginAt(user.getLastLoginAt());
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        return User.withId(
                UserId.from(entity.getId()),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFirstName(),
                entity.getLastName(),
                Role.valueOf(entity.getRole()),
                entity.isActive(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getDateOfBirth(),
                entity.getAvatar(),
                entity.getLastLoginAt()
        );
    }
}
