package com.medipet.medipet.clinical.infrastructure.inbound.rest.mapper;

import com.medipet.medipet.clinical.application.ConsultationSummary;
import com.medipet.medipet.clinical.infrastructure.inbound.rest.dto.response.ConsultationResponse;

public class ConsultationMapper {

    private ConsultationMapper() {}

    public static ConsultationResponse toResponse(ConsultationSummary summary) {
        return new ConsultationResponse(
                summary.id(),
                summary.appointmentId(),
                summary.petId(),
                summary.petName(),
                summary.clientId(),
                summary.clientName(),
                summary.veterinarianId(),
                summary.veterinarianName(),
                summary.weight(),
                summary.temperature(),
                summary.symptoms(),
                summary.diagnosis(),
                summary.treatment(),
                summary.notes(),
                summary.active(),
                summary.createdAt(),
                summary.updatedAt()
        );
    }
}
