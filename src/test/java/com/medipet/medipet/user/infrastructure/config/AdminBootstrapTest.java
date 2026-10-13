package com.medipet.medipet.user.infrastructure.config;

import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.port.inbound.CreateUserUseCase;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreateUserUseCase createUserUseCase;

    private final ApplicationArguments args = new DefaultApplicationArguments();

    @Test
    void shouldSkipWhenCredentialsAreMissing() {
        AdminBootstrap bootstrap = new AdminBootstrap(userRepository, createUserUseCase, "", "");

        bootstrap.run(args);

        verifyNoInteractions(userRepository);
        verifyNoInteractions(createUserUseCase);
    }

    @Test
    void shouldSkipWhenAdminAlreadyExists() {
        AdminBootstrap bootstrap = new AdminBootstrap(userRepository, createUserUseCase,
                "admin@medipet.com", "secret-password");
        when(userRepository.existsByEmail("admin@medipet.com")).thenReturn(true);

        bootstrap.run(args);

        verify(userRepository).existsByEmail("admin@medipet.com");
        verifyNoInteractions(createUserUseCase);
    }

    @Test
    void shouldCreateAdminWhenConfiguredAndAbsent() {
        AdminBootstrap bootstrap = new AdminBootstrap(userRepository, createUserUseCase,
                "admin@medipet.com", "secret-password");
        when(userRepository.existsByEmail("admin@medipet.com")).thenReturn(false);

        bootstrap.run(args);

        verify(userRepository).existsByEmail("admin@medipet.com");
        verify(createUserUseCase).execute("admin@medipet.com", "secret-password",
                "Admin", "System", Role.ADMIN, null, null, null);
    }
}
