package com.medipet.medipet.user.infrastructure.outbound.security;

import com.medipet.medipet.user.infrastructure.outbound.persistence.repository.UserJpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    SecurityUserDetailsService(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return User.builder()
                .username(entity.getEmail())
                .password(entity.getPassword())
                .authorities("ROLE_" + entity.getRole())
                .accountLocked(!entity.isActive())
                .build();
    }
}
