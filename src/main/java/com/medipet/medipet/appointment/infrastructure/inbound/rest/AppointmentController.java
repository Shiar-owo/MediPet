package com.medipet.medipet.appointment.infrastructure.inbound.rest;

import com.medipet.medipet.appointment.application.port.inbound.AppointmentQueryService;
import com.medipet.medipet.appointment.application.port.inbound.CancelAppointmentUseCase;
import com.medipet.medipet.appointment.application.port.inbound.CreateAppointmentUseCase;
import com.medipet.medipet.appointment.application.port.inbound.UpdateAppointmentUseCase;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.request.AppointmentRequest;
import com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.request.UpdateAppointmentRequest;
import com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.response.AppointmentResponse;
import com.medipet.medipet.appointment.infrastructure.inbound.rest.mapper.AppointmentMapper;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.user.domain.valueobject.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Appointment scheduling and management")
class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final UpdateAppointmentUseCase updateAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;
    private final AppointmentQueryService appointmentQueryService;

    AppointmentController(CreateAppointmentUseCase createAppointmentUseCase,
                          UpdateAppointmentUseCase updateAppointmentUseCase,
                          CancelAppointmentUseCase cancelAppointmentUseCase,
                          AppointmentQueryService appointmentQueryService) {
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.updateAppointmentUseCase = updateAppointmentUseCase;
        this.cancelAppointmentUseCase = cancelAppointmentUseCase;
        this.appointmentQueryService = appointmentQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new appointment", description = "Schedules a new appointment. Checks for time overlap with existing appointments for the same veterinarian.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or endTime before startTime", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Veterinarian cannot create appointments for another vet", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pet, client, or veterinarian not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Time overlap with existing appointment", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(@Valid @RequestBody AppointmentRequest request) {
        var appointment = createAppointmentUseCase.execute(
                LocalDate.parse(request.getDate()),
                LocalTime.parse(request.getStartTime()),
                LocalTime.parse(request.getEndTime()),
                request.getReason(),
                PetId.from(request.getPetId()),
                ClientId.from(request.getClientId()),
                UserId.from(request.getVeterinarianId()));

        var summary = appointmentQueryService.findById(appointment.getId());
        var response = AppointmentMapper.toResponse(summary);
        URI location = URI.create("/api/appointments/" + response.getId());
        return ResponseEntity.created(location).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an appointment", description = "Updates appointment fields. Re-checks overlap if date or time is changed.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or endTime before startTime", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Veterinarian can only update own appointments", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Time overlap with existing appointment", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<AppointmentResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateAppointmentRequest request) {
        var appointment = updateAppointmentUseCase.execute(
                AppointmentId.from(id),
                request.getDate() != null ? LocalDate.parse(request.getDate()) : null,
                request.getStartTime() != null ? LocalTime.parse(request.getStartTime()) : null,
                request.getEndTime() != null ? LocalTime.parse(request.getEndTime()) : null,
                request.getReason());

        var summary = appointmentQueryService.findById(appointment.getId());
        return ResponseEntity.ok(ApiResponse.ok(AppointmentMapper.toResponse(summary)));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment", description = "Cancels an appointment with an optional reason.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment cancelled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Appointment already cancelled", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Veterinarian can only cancel own appointments", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancel(
            @PathVariable String id,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        String cancelReason = body != null ? body.get("cancelReason") : null;
        var appointment = cancelAppointmentUseCase.execute(AppointmentId.from(id), cancelReason);

        var summary = appointmentQueryService.findById(appointment.getId());
        return ResponseEntity.ok(ApiResponse.ok(AppointmentMapper.toResponse(summary)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Returns a single appointment by its UUID.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appointment found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<ApiResponse<AppointmentResponse>> getById(@PathVariable String id) {
        var summary = appointmentQueryService.findById(AppointmentId.from(id));
        return ResponseEntity.ok(ApiResponse.ok(AppointmentMapper.toResponse(summary)));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "List appointments by client", description = "Returns paginated appointments for a specific client.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paginated list of appointments")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getByClientId(
            @PathVariable String clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var summaries = appointmentQueryService.findByClientId(
                ClientId.from(clientId), PageRequest.of(page, size));
        var response = new PageResponse<>(
                summaries.getContent().stream().map(AppointmentMapper::toResponse).toList(),
                summaries.getPage(),
                summaries.getSize(),
                summaries.getTotalElements(),
                summaries.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "List appointments by pet", description = "Returns paginated appointments for a specific pet.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paginated list of appointments")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getByPetId(
            @PathVariable String petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var summaries = appointmentQueryService.findByPetId(
                PetId.from(petId), PageRequest.of(page, size));
        var response = new PageResponse<>(
                summaries.getContent().stream().map(AppointmentMapper::toResponse).toList(),
                summaries.getPage(),
                summaries.getSize(),
                summaries.getTotalElements(),
                summaries.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/veterinarian/{vetId}")
    @Operation(summary = "List appointments by veterinarian", description = "Returns paginated appointments for a specific veterinarian.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paginated list of appointments")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getByVeterinarianId(
            @PathVariable String vetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var summaries = appointmentQueryService.findByVeterinarianId(UserId.from(vetId), PageRequest.of(page, size));
        var response = new PageResponse<>(
                summaries.getContent().stream().map(AppointmentMapper::toResponse).toList(),
                summaries.getPage(),
                summaries.getSize(),
                summaries.getTotalElements(),
                summaries.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @Operation(summary = "List/search appointments", description = "Returns a paginated list. Optional ?date= filters by date.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paginated list of appointments")
    public ResponseEntity<ApiResponse<PageResponse<AppointmentResponse>>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size);
        var summaries = date != null
                ? appointmentQueryService.findByDateRange(date, date, pageable)
                : appointmentQueryService.findAll(pageable);
        var response = new PageResponse<>(
                summaries.getContent().stream().map(AppointmentMapper::toResponse).toList(),
                summaries.getPage(),
                summaries.getSize(),
                summaries.getTotalElements(),
                summaries.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
