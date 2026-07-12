package com.medipet.medipet.appointment.domain.model;

import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Appointment {

    private AppointmentId id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private AppointmentStatus status;
    private String cancelReason;
    private PetId petId;
    private ClientId clientId;
    private UserId veterinarianId;
    private boolean active;

    private Appointment() {
    }

    private Appointment(AppointmentId id, LocalDate date, LocalTime startTime, LocalTime endTime,
                        String reason, AppointmentStatus status, String cancelReason,
                        PetId petId, ClientId clientId, UserId veterinarianId, boolean active) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.status = status;
        this.cancelReason = cancelReason;
        this.petId = petId;
        this.clientId = clientId;
        this.veterinarianId = veterinarianId;
        this.active = active;
    }

    public static Appointment create(LocalDate date, LocalTime startTime, LocalTime endTime,
                                     String reason, PetId petId, ClientId clientId,
                                     UserId veterinarianId) {
        return new Appointment(AppointmentId.generate(), date, startTime, endTime,
                reason, AppointmentStatus.PENDIENTE, null,
                petId, clientId, veterinarianId, true);
    }

    public static Appointment withId(AppointmentId id, LocalDate date, LocalTime startTime,
                                     LocalTime endTime, String reason, AppointmentStatus status,
                                     String cancelReason, PetId petId, ClientId clientId,
                                     UserId veterinarianId, boolean active) {
        return new Appointment(id, date, startTime, endTime, reason, status, cancelReason,
                petId, clientId, veterinarianId, active);
    }

    public AppointmentId getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public PetId getPetId() {
        return petId;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public UserId getVeterinarianId() {
        return veterinarianId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
