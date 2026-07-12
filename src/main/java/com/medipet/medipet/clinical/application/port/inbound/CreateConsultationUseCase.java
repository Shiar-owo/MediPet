package com.medipet.medipet.clinical.application.port.inbound;

import com.medipet.medipet.clinical.domain.model.Consultation;

import java.math.BigDecimal;

public interface CreateConsultationUseCase {

    record Command(
            java.util.UUID appointmentId,
            java.util.UUID petId,
            java.util.UUID clientId,
            java.util.UUID veterinarianId,
            BigDecimal weight,
            BigDecimal temperature,
            String symptoms,
            String diagnosis,
            String treatment,
            String notes
    ) {}

    Consultation execute(Command command);
}
