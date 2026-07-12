package com.medipet.medipet.user.infrastructure.inbound.rest;

import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.ForbiddenException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.shared.port.outbound.FileStoragePort;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.port.inbound.ActivateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.DeactivateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.UpdateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.UserQueryService;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.request.UpdateUserRequest;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.response.UserResponse;
import com.medipet.medipet.user.infrastructure.inbound.rest.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management (ADMIN only)")
class UserController {

    private final UserQueryService userQueryService;
    private final UpdateUserUseCase updateUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;
    private final FileStoragePort fileStoragePort;
    private final UserRepository userRepository;

    UserController(UserQueryService userQueryService,
                   UpdateUserUseCase updateUserUseCase,
                   ActivateUserUseCase activateUserUseCase,
                   DeactivateUserUseCase deactivateUserUseCase,
                   FileStoragePort fileStoragePort,
                   UserRepository userRepository) {
        this.userQueryService = userQueryService;
        this.updateUserUseCase = updateUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.deactivateUserUseCase = deactivateUserUseCase;
        this.fileStoragePort = fileStoragePort;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "List users", description = "Returns a paginated list of all users.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paginated list of users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<com.medipet.medipet.user.domain.model.User> users =
                userQueryService.findAll(PageRequest.of(page, size));
        PageResponse<UserResponse> response = new PageResponse<>(
                users.getContent().stream().map(UserMapper::toResponse).toList(),
                users.getPage(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/veterinarians")
    @Operation(summary = "List veterinarians", description = "Returns a paginated list of all users with VETERINARIO role.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paginated list of veterinarians")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getVeterinarians(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<com.medipet.medipet.user.domain.model.User> vets =
                userQueryService.findByRole(Role.VETERINARIO, PageRequest.of(page, size));
        PageResponse<UserResponse> response = new PageResponse<>(
                vets.getContent().stream().map(UserMapper::toResponse).toList(),
                vets.getPage(),
                vets.getSize(),
                vets.getTotalElements(),
                vets.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns a single user by their UUID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable String id) {
        var user = userQueryService.findById(UserId.from(id));
        return ResponseEntity.ok(ApiResponse.ok(UserMapper.toResponse(user)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates user profile fields. Only provided fields are changed.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        var user = updateUserUseCase.execute(
                UserId.from(id),
                request.getFirstName(),
                request.getLastName(),
                request.getRole() != null ? request.getRole().toUpperCase() : null,
                request.getPhone(),
                request.getAddress(),
                null);
        return ResponseEntity.ok(ApiResponse.ok(UserMapper.toResponse(user)));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivates a user account. Deactivated users cannot log in.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deactivated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String id) {
        deactivateUserUseCase.execute(UserId.from(id));
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate user", description = "Reactivates a previously deactivated user account.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User activated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable String id) {
        activateUserUseCase.execute(UserId.from(id));
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload user avatar",
            description = "Uploads an avatar photo for a user. Accepted types: JPEG, PNG, GIF, WebP. Max size: 5MB. The photo is stored in Cloudinary and the URL is saved to the user's avatar field.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Avatar uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file type or size exceeds 5MB",
                    content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Cannot upload avatar for another user",
                    content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @Parameter(description = "User UUID", required = true)
            @PathVariable String id,
            @Parameter(description = "Avatar file (JPEG, PNG, GIF, WebP. Max 5MB)", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        var user = userRepository.findById(UserId.from(id))
                .orElseThrow(() -> new NotFoundException("User", id));

        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin && !authentication.getName().equals(user.getEmail())) {
            throw new ForbiddenException("You can only upload your own avatar");
        }

        String avatarUrl = fileStoragePort.upload(file, "medipet/avatars");
        user.setAvatar(avatarUrl);
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.ok(Map.of("avatarUrl", avatarUrl)));
    }
}
