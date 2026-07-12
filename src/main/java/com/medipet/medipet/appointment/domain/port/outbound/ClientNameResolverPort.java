package com.medipet.medipet.appointment.domain.port.outbound;

import com.medipet.medipet.client.domain.valueobject.ClientId;

import java.util.Collection;
import java.util.Map;

public interface ClientNameResolverPort {
    Map<ClientId, String> resolveNames(Collection<ClientId> ids);
}
