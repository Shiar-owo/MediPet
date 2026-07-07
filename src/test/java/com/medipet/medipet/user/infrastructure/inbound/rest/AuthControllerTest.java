package com.medipet.medipet.user.infrastructure.inbound.rest;

import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.inbound.AuthService;
import com.medipet.medipet.user.domain.port.inbound.CreateUserUseCase;
import com.medipet.medipet.user.domain.valueobject.UserId;
import com.medipet.medipet.user.infrastructure.inbound.security.JwtAuthenticationFilter;
import com.medipet.medipet.user.infrastructure.outbound.security.SecurityUserDetailsService;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        when(authService.issueTokens("user@example.com"))
                .thenReturn(new AuthService.LoginResult("access-token", "refresh-token", "VETERINARIO"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        User user = User.withId(
                UserId.from(UUID.randomUUID()), "new@example.com", "encoded",
                "Jane", "Doe", Role.VETERINARIO, true, null, null, null, null, null);

        when(createUserUseCase.execute("new@example.com", "password123",
                "Jane", "Doe", Role.VETERINARIO)).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"new@example.com","password":"password123","firstName":"Jane","lastName":"Doe","role":"VETERINARIO"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        when(authService.refreshToken("old-refresh-token"))
                .thenReturn(new AuthService.LoginResult("new-access", "new-refresh", "ADMIN"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"old-refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"));
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"some-refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(authService).logout("some-refresh-token");
    }

    @Test
    void shouldRejectLoginWithBlankEmail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest());
    }
}
