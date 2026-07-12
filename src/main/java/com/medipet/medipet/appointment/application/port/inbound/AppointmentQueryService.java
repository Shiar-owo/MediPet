package com.medipet.medipet.appointment.application.port.inbound;

import com.medipet.medipet.appointment.application.AppointmentSummary;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AppointmentQueryService {
    AppointmentSummary findById(AppointmentId id);
    PageResponse<AppointmentSummary> findByClientId(ClientId clientId, Pageable pageable);
    PageResponse<AppointmentSummary> findByPetId(PetId petId, Pageable pageable);
    PageResponse<AppointmentSummary> findByVeterinarianId(UserId veterinarianId, Pageable pageable);
    PageResponse<AppointmentSummary> findByVeterinarianIdAndClientId(UserId veterinarianId, ClientId clientId, Pageable pageable);
    PageResponse<AppointmentSummary> findByVeterinarianIdAndPetId(UserId veterinarianId, PetId petId, Pageable pageable);
    PageResponse<AppointmentSummary> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    PageResponse<AppointmentSummary> findAll(Pageable pageable);
}
