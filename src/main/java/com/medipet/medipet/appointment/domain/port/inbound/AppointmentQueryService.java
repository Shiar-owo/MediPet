package com.medipet.medipet.appointment.domain.port.inbound;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AppointmentQueryService {
    Appointment findById(AppointmentId id);
    PageResponse<Appointment> findByClientId(ClientId clientId, Pageable pageable);
    PageResponse<Appointment> findByPetId(PetId petId, Pageable pageable);
    PageResponse<Appointment> findByVeterinarianId(UserId veterinarianId, Pageable pageable);
    PageResponse<Appointment> findByVeterinarianIdAndClientId(UserId veterinarianId, ClientId clientId, Pageable pageable);
    PageResponse<Appointment> findByVeterinarianIdAndPetId(UserId veterinarianId, PetId petId, Pageable pageable);
    PageResponse<Appointment> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    PageResponse<Appointment> findAll(String query, Pageable pageable);

    PageResponse<Appointment> findForCurrentUserByClientId(ClientId clientId, Pageable pageable);
    PageResponse<Appointment> findForCurrentUserByPetId(PetId petId, Pageable pageable);
    PageResponse<Appointment> findForCurrentUser(LocalDate date, Pageable pageable);
}
