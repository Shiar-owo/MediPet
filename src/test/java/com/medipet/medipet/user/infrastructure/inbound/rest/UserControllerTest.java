package com.medipet.medipet.user.infrastructure.inbound.rest;

import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.inbound.ActivateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.DeactivateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.UpdateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.UserQueryService;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import com.medipet.medipet.user.domain.valueobject.UserId;
import com.medipet.medipet.user.infrastructure.inbound.security.JwtAuthenticationFilter;
import com.medipet.medipet.user.infrastructure.outbound.security.SecurityUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserQueryService userQueryService;

    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;

    @MockitoBean
    private ActivateUserUseCase activateUserUseCase;

    @MockitoBean
    private DeactivateUserUseCase deactivateUserUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User createTestUser(String id, String email) {
        return User.withId(
                UserId.from(UUID.fromString(id)), email, "hashed",
                "John", "Doe", Role.VETERINARIO, true, "123", null, null, null, null);
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        String id = UUID.randomUUID().toString();
        User user = createTestUser(id, "user@example.com");
        PageResponse<User> page = new PageResponse<>(List.of(user), 0, 20, 1, 1);

        when(userQueryService.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/users").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].email").value("user@example.com"));
    }

    @Test
    void shouldGetVeterinarians() throws Exception {
        String id = UUID.randomUUID().toString();
        User vet = createTestUser(id, "vet@medipet.com");
        PageResponse<User> page = new PageResponse<>(List.of(vet), 0, 20, 1, 1);

        when(userQueryService.findByRole(eq(Role.VETERINARIO), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/users/veterinarians").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].email").value("vet@medipet.com"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        String id = UUID.randomUUID().toString();
        User user = createTestUser(id, "user@example.com");

        when(userQueryService.findById(UserId.from(id))).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        String id = UUID.randomUUID().toString();
        when(userQueryService.findById(UserId.from(id)))
                .thenThrow(new NotFoundException("User", id));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        String id = UUID.randomUUID().toString();
        User updatedUser = User.withId(
                UserId.from(UUID.fromString(id)), "user@example.com", "hashed",
                "Jane", "Doe", Role.VETERINARIO, true, "999", null, null, null, null);

        when(updateUserUseCase.execute(UserId.from(id), "Jane", null, null, null, null, null))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Jane"));
    }

    @Test
    void shouldDeactivateUser() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(patch("/api/users/{id}/deactivate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(deactivateUserUseCase).execute(UserId.from(id));
    }

    @Test
    void shouldActivateUser() throws Exception {
        String id = UUID.randomUUID().toString();

        mockMvc.perform(patch("/api/users/{id}/activate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(activateUserUseCase).execute(UserId.from(id));
    }
}
