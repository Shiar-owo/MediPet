package com.medipet.medipet.user.domain.port.inbound;

import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;

import java.time.LocalDate;

public interface CreateUserUseCase {

    User execute(String email, String password, String firstName, String lastName, Role role,
                 String phone, String address, LocalDate dateOfBirth);
}
