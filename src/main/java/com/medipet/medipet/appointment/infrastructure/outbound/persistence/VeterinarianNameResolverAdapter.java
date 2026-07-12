package com.medipet.medipet.appointment.infrastructure.outbound.persistence;

import com.medipet.medipet.appointment.domain.port.outbound.VeterinarianNameResolverPort;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
class VeterinarianNameResolverAdapter implements VeterinarianNameResolverPort {

    private final UserRepository userRepository;

    VeterinarianNameResolverAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Map<UserId, String> resolveNames(Collection<UserId> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> u.getFirstName() + " " + u.getLastName(),
                        (a, b) -> a));
    }
}
