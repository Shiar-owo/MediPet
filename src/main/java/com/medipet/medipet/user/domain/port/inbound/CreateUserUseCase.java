package com.medipet.medipet.user.domain.port.inbound;

import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;

public interface CreateUserUseCase {

    User execute(String email, String password, String firstName, String lastName, Role role);
}
