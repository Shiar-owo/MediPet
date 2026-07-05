package com.medipet.medipet.user.domain.port.inbound;

import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.valueobject.UserId;

public interface UpdateUserUseCase {

    User execute(UserId id, String firstName, String lastName, String role, String phone, String address);
}
