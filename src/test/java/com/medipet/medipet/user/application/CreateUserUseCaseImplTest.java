package com.medipet.medipet.user.application;

import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.PasswordEncoder;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CreateUserUseCaseImpl createUserUseCase;

    @BeforeEach
    void setUp() {
        createUserUseCase = new CreateUserUseCaseImpl(userRepository, passwordEncoder);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return User.withId(u.getId(), u.getEmail(), u.getPassword(), u.getFirstName(),
                    u.getLastName(), u.getRole(), u.isActive(), null, null, null, null, null);
        });

        User result = createUserUseCase.execute("test@example.com", "password123",
                "John", "Doe", Role.VETERINARIO, "+549115551234", "Av. Corrientes 1234", null);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.VETERINARIO, result.getRole());
        assertTrue(result.isActive());
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowConflictWhenEmailExists() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                createUserUseCase.execute("existing@example.com", "password123",
                        "John", "Doe", Role.RECEPCIONISTA, null, null, null));

        verify(userRepository).existsByEmail("existing@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }
}
