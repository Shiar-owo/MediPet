package com.medipet.medipet.user.application;

import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.PasswordEncoder;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ChangePasswordUseCaseImpl changePasswordUseCase;

    @BeforeEach
    void setUp() {
        changePasswordUseCase = new ChangePasswordUseCaseImpl(userRepository, passwordEncoder);
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        UserId userId = UserId.generate();
        User user = User.create("test@example.com", "old_hashed", "John", "Doe", Role.VETERINARIO);
        User userWithId = User.withId(userId, user.getEmail(), user.getPassword(),
                user.getFirstName(), user.getLastName(), user.getRole(), user.isActive(),
                null, null, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithId));
        when(passwordEncoder.matches("oldPassword", "old_hashed")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("new_hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        changePasswordUseCase.execute(userId, "oldPassword", "newPassword");

        verify(passwordEncoder).matches("oldPassword", "old_hashed");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        UserId userId = UserId.generate();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                changePasswordUseCase.execute(userId, "oldPassword", "newPassword"));
    }

    @Test
    void shouldThrowWhenOldPasswordIncorrect() {
        UserId userId = UserId.generate();
        User userWithId = User.withId(userId, "test@example.com", "old_hashed",
                "John", "Doe", Role.VETERINARIO, true, null, null, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithId));
        when(passwordEncoder.matches("wrongPassword", "old_hashed")).thenReturn(false);

        assertThrows(BadRequestException.class, () ->
                changePasswordUseCase.execute(userId, "wrongPassword", "newPassword"));

        verify(userRepository, never()).save(any());
    }
}
