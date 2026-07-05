package com.medipet.medipet.user.domain.port.inbound;

import com.medipet.medipet.user.domain.valueobject.UserId;

public interface ActivateUserUseCase {

    void execute(UserId id);
}
