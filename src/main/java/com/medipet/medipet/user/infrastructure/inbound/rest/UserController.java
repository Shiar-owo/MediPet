package com.medipet.medipet.user.infrastructure.inbound.rest;

import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.user.domain.port.inbound.ActivateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.DeactivateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.UpdateUserUseCase;
import com.medipet.medipet.user.domain.port.inbound.UserQueryService;
import com.medipet.medipet.user.domain.valueobject.UserId;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.request.UpdateUserRequest;
import com.medipet.medipet.user.infrastructure.inbound.rest.dto.response.UserResponse;
import com.medipet.medipet.user.infrastructure.inbound.rest.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
class UserController {

    private final UserQueryService userQueryService;
    private final UpdateUserUseCase updateUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;

    UserController(UserQueryService userQueryService,
                   UpdateUserUseCase updateUserUseCase,
                   ActivateUserUseCase activateUserUseCase,
                   DeactivateUserUseCase deactivateUserUseCase) {
        this.userQueryService = userQueryService;
        this.updateUserUseCase = updateUserUseCase;
        this.activateUserUseCase = activateUserUseCase;
        this.deactivateUserUseCase = deactivateUserUseCase;
    }

    @GetMapping
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable String id) {
        var user = userQueryService.findById(UserId.from(id));
        return ResponseEntity.ok(ApiResponse.ok(UserMapper.toResponse(user)));
    }

    @PutMapping("/{id}")
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
                request.getAvatar());
        return ResponseEntity.ok(ApiResponse.ok(UserMapper.toResponse(user)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String id) {
        deactivateUserUseCase.execute(UserId.from(id));
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable String id) {
        activateUserUseCase.execute(UserId.from(id));
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
