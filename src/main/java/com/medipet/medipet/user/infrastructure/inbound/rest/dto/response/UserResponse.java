package com.medipet.medipet.user.infrastructure.inbound.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private boolean active;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String avatar;
    private LocalDateTime lastLoginAt;
}
