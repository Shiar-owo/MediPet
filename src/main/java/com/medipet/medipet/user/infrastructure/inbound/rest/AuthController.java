package com.medipet.medipet.user.infrastructure.inbound.rest;

import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.port.inbound.AuthService;
import com.medipet.medipet.user.domain.port.inbound.CreateUserUseCase;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.request.LoginRequest;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.request.RefreshTokenRequest;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.request.RegisterUserRequest;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.response.LoginResponse;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.response.RefreshTokenResponse;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.response.UserResponse;
import com.medipet.medipet.user.infrastructure.inbound.rest.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication and user registration")
class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final CreateUserUseCase createUserUseCase;

    AuthController(AuthenticationManager authenticationManager,
                   AuthService authService,
                   CreateUserUseCase createUserUseCase) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account. Requires ADMIN role.")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
        Role role = request.getRole() != null
                ? Role.valueOf(request.getRole().toUpperCase())
                : Role.RECEPCIONISTA;

        var user = createUserUseCase.execute(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                role);

        var response = UserMapper.toResponse(user);
        URI location = URI.create("/api/users/" + response.getId());
        return ResponseEntity.created(location).body(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns JWT access and refresh tokens.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        AuthService.LoginResult result = authService.issueTokens(request.getEmail());

        return ResponseEntity.ok(ApiResponse.ok(new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                request.getEmail(),
                result.role())));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Exchanges a valid refresh token for a new access/refresh token pair.")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthService.LoginResult result = authService.refreshToken(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.ok(new RefreshTokenResponse(
                result.accessToken(),
                result.refreshToken())));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates a refresh token.")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.ok());
    }
}
