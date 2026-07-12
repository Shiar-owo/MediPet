package com.medipet.medipet.clinical.domain.port.outbound;

import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ConsultationRepository {

    Consultation save(Consultation consultation);

    Optional<Consultation> findById(ConsultationId id);

    Optional<Consultation> findByAppointmentId(AppointmentId appointmentId);

    Page<Consultation> findByPetId(PetId petId, Pageable pageable);
}
