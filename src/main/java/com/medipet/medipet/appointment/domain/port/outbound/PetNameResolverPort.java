package com.medipet.medipet.appointment.domain.port.outbound;

import com.medipet.medipet.pet.domain.valueobject.PetId;

import java.util.Collection;
import java.util.Map;

public interface PetNameResolverPort {
    Map<PetId, String> resolveNames(Collection<PetId> ids);
}
