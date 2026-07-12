package com.medipet.medipet.clinical.application.port.inbound;

import com.medipet.medipet.clinical.application.ConsultationSummary;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConsultationQueryService {

    ConsultationSummary getById(ConsultationId id);

    ConsultationSummary getByAppointmentId(AppointmentId appointmentId);

    Page<ConsultationSummary> getByPetId(PetId petId, Pageable pageable);
}
