package com.medipet.medipet.appointment.domain.port.outbound;

import com.medipet.medipet.user.domain.valueobject.UserId;

import java.util.Collection;
import java.util.Map;

public interface VeterinarianNameResolverPort {
    Map<UserId, String> resolveNames(Collection<UserId> ids);
}
