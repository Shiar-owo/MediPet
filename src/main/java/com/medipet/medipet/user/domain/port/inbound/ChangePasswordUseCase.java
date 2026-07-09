package com.medipet.medipet.user.domain.port.inbound;

import com.medipet.medipet.user.domain.valueobject.UserId;

public interface ChangePasswordUseCase {

    void execute(UserId id, String oldPassword, String newPassword);
}
