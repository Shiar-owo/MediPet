package com.medipet.medipet.user.infrastructure.inbound.rest.dto.request;

import lombok.Getter;

@Getter
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String role;
    private String phone;
    private String address;
    private String avatar;
}
