package com.medipet.medipet.appointment.domain.port.outbound;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(AppointmentId id);
    List<Appointment> findOverlapping(UserId veterinarianId, LocalDate date,
                                      java.time.LocalTime startTime, java.time.LocalTime endTime);
    Page<Appointment> findByClientId(ClientId clientId, Pageable pageable);
    Page<Appointment> findByPetId(PetId petId, Pageable pageable);
    Page<Appointment> findByVeterinarianId(UserId veterinarianId, Pageable pageable);
    Page<Appointment> findByVeterinarianIdAndClientId(UserId veterinarianId, ClientId clientId, Pageable pageable);
    Page<Appointment> findByVeterinarianIdAndPetId(UserId veterinarianId, PetId petId, Pageable pageable);
    Page<Appointment> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Appointment> findAll(Pageable pageable);
}
