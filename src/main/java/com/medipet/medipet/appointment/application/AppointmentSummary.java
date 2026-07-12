package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class AppointmentSummary {

    private final AppointmentId id;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String reason;
    private final AppointmentStatus status;
    private final String cancelReason;
    private final PetId petId;
    private final String petName;
    private final ClientId clientId;
    private final String clientName;
    private final UserId veterinarianId;
    private final String veterinarianName;
    private final boolean active;

    private AppointmentSummary(AppointmentId id, LocalDate date, LocalTime startTime, LocalTime endTime,
                               String reason, AppointmentStatus status, String cancelReason,
                               PetId petId, String petName, ClientId clientId, String clientName,
                               UserId veterinarianId, String veterinarianName, boolean active) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.status = status;
        this.cancelReason = cancelReason;
        this.petId = petId;
        this.petName = petName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.veterinarianId = veterinarianId;
        this.veterinarianName = veterinarianName;
        this.active = active;
    }

    public static AppointmentSummary from(Appointment appointment,
                                          Map<PetId, String> petNames,
                                          Map<ClientId, String> clientNames,
                                          Map<UserId, String> vetNames) {
        return new AppointmentSummary(
                appointment.getId(),
                appointment.getDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getCancelReason(),
                appointment.getPetId(),
                petNames.getOrDefault(appointment.getPetId(), null),
                appointment.getClientId(),
                clientNames.getOrDefault(appointment.getClientId(), null),
                appointment.getVeterinarianId(),
                vetNames.getOrDefault(appointment.getVeterinarianId(), null),
                appointment.isActive());
    }

    public AppointmentId getId() { return id; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getReason() { return reason; }
    public AppointmentStatus getStatus() { return status; }
    public String getCancelReason() { return cancelReason; }
    public PetId getPetId() { return petId; }
    public String getPetName() { return petName; }
    public ClientId getClientId() { return clientId; }
    public String getClientName() { return clientName; }
    public UserId getVeterinarianId() { return veterinarianId; }
    public String getVeterinarianName() { return veterinarianName; }
    public boolean isActive() { return active; }
}
