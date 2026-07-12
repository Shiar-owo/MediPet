package com.medipet.medipet.clinical.infrastructure.inbound.rest;

import com.medipet.medipet.clinical.application.ConsultationSummary;
import com.medipet.medipet.clinical.application.port.inbound.ConsultationQueryService;
import com.medipet.medipet.clinical.application.port.inbound.CreateConsultationUseCase;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.clinical.infrastructure.inbound.rest.dto.request.CreateConsultationRequest;
import com.medipet.medipet.clinical.infrastructure.inbound.rest.dto.response.ConsultationResponse;
import com.medipet.medipet.clinical.infrastructure.inbound.rest.mapper.ConsultationMapper;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
@Tag(name = "Consultations", description = "Medical consultations management")
class ConsultationController {

    private final CreateConsultationUseCase createConsultationUseCase;
    private final ConsultationQueryService consultationQueryService;

    ConsultationController(CreateConsultationUseCase createConsultationUseCase,
                           ConsultationQueryService consultationQueryService) {
        this.createConsultationUseCase = createConsultationUseCase;
        this.consultationQueryService = consultationQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a medical consultation")
    public ResponseEntity<ApiResponse<ConsultationResponse>> create(
            @Valid @RequestBody CreateConsultationRequest request) {
        var command = new CreateConsultationUseCase.Command(
                request.appointmentId(),
                request.petId(),
                request.clientId(),
                request.veterinarianId(),
                request.weight(),
                request.temperature(),
                request.symptoms(),
                request.diagnosis(),
                request.treatment(),
                request.notes()
        );
        Consultation consultation = createConsultationUseCase.execute(command);
        ConsultationSummary summary = consultationQueryService.getById(consultation.getId());
        ConsultationResponse response = ConsultationMapper.toResponse(summary);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get consultation by ID")
    public ResponseEntity<ApiResponse<ConsultationResponse>> getById(
            @Parameter(description = "Consultation ID") @PathVariable String id) {
        ConsultationSummary summary = consultationQueryService.getById(ConsultationId.from(id));
        ConsultationResponse response = ConsultationMapper.toResponse(summary);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get consultation by appointment ID")
    public ResponseEntity<ApiResponse<ConsultationResponse>> getByAppointmentId(
            @Parameter(description = "Appointment ID") @PathVariable String appointmentId) {
        ConsultationSummary summary = consultationQueryService.getByAppointmentId(AppointmentId.from(appointmentId));
        ConsultationResponse response = ConsultationMapper.toResponse(summary);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Get consultation history by pet ID")
    public ResponseEntity<ApiResponse<PageResponse<ConsultationResponse>>> getByPetId(
            @Parameter(description = "Pet ID") @PathVariable String petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ConsultationSummary> summaries = consultationQueryService.getByPetId(
                PetId.from(petId), PageRequest.of(page, size));
        var responses = new PageResponse<>(
                summaries.getContent().stream().map(ConsultationMapper::toResponse).toList(),
                summaries.getNumber(),
                summaries.getSize(),
                summaries.getTotalElements(),
                summaries.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }
}
