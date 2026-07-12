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
import io.swagger.v3.oas.annotations.media.Content;
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
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {
        Role role = request.getRole() != null
                ? Role.valueOf(request.getRole().toUpperCase())
                : Role.RECEPCIONISTA;

        var user = createUserUseCase.execute(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                role,
                request.getPhone(),
                request.getAddress(),
                request.getDateOfBirth(),
                request.getAvatar());

        var response = UserMapper.toResponse(user);
        URI location = URI.create("/api/users/" + response.getId());
        return ResponseEntity.created(location).body(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns JWT access and refresh tokens.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        AuthService.LoginResult result = authService.issueTokens(request.getEmail());

        return ResponseEntity.ok(ApiResponse.ok(new LoginResponse(
                result.userId(),
                result.accessToken(),
                result.refreshToken(),
                result.email(),
                result.role())));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Exchanges a valid refresh token for a new access/refresh token pair.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthService.LoginResult result = authService.refreshToken(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.ok(new RefreshTokenResponse(
                result.userId(),
                result.accessToken(),
                result.refreshToken(),
                result.email(),
                result.role())));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates a refresh token.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logged out successfully")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.ok());
    }
}
