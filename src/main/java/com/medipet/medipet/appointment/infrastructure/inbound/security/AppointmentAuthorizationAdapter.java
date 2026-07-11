package com.medipet.medipet.appointment.infrastructure.inbound.security;

import com.medipet.medipet.appointment.domain.port.inbound.AppointmentAuthorizationPort;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
class AppointmentAuthorizationAdapter implements AppointmentAuthorizationPort {

    private final UserRepository userRepository;

    AppointmentAuthorizationAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserId getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NotFoundException("User", email));
    }

    @Override
    public Role getCurrentUserRole() {
        String authority = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority();
        return Role.valueOf(authority.replace("ROLE_", ""));
    }
}
