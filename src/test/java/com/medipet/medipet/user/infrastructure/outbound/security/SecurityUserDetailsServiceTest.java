package com.medipet.medipet.user.infrastructure.outbound.security;

import com.medipet.medipet.user.infrastructure.outbound.persistence.entity.UserEntity;
import com.medipet.medipet.user.infrastructure.outbound.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUserDetailsServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    private SecurityUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new SecurityUserDetailsService(userRepository);
    }

    @Test
    void shouldLoadUserByUsername() {
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail("user@example.com");
        entity.setPassword("hashed_password");
        entity.setRole("VETERINARIO");
        entity.setActive(true);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(entity));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("hashed_password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VETERINARIO")));
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void shouldReturnLockedAccountWhenInactive() {
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail("inactive@example.com");
        entity.setPassword("hashed_password");
        entity.setRole("RECEPCIONISTA");
        entity.setActive(false);

        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(entity));

        UserDetails userDetails = userDetailsService.loadUserByUsername("inactive@example.com");

        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@example.com"));
    }
}
