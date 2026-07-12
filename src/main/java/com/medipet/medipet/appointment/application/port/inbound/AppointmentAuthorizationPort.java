package com.medipet.medipet.appointment.application.port.inbound;

import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.valueobject.UserId;

public interface AppointmentAuthorizationPort {

    UserId getCurrentUserId();

    Role getCurrentUserRole();
}
