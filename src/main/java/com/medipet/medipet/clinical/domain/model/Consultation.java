package com.medipet.medipet.clinical.domain.model;

import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;

import java.math.BigDecimal;
import java.util.Objects;

public class Consultation {

    private final ConsultationId id;
    private final AppointmentId appointmentId;
    private final PetId petId;
    private final ClientId clientId;
    private final UserId veterinarianId;
    private BigDecimal weight;
    private BigDecimal temperature;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String notes;
    private boolean active;

    private Consultation(ConsultationId id, AppointmentId appointmentId, PetId petId,
                         ClientId clientId, UserId veterinarianId, BigDecimal weight,
                         BigDecimal temperature, String symptoms, String diagnosis,
                         String treatment, String notes, boolean active) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.petId = petId;
        this.clientId = clientId;
        this.veterinarianId = veterinarianId;
        this.weight = weight;
        this.temperature = temperature;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.notes = notes;
        this.active = active;
    }

    public static Consultation create(AppointmentId appointmentId, PetId petId,
                                      ClientId clientId, UserId veterinarianId,
                                      BigDecimal weight, BigDecimal temperature,
                                      String symptoms, String diagnosis,
                                      String treatment, String notes) {
        return new Consultation(ConsultationId.generate(), appointmentId, petId,
                clientId, veterinarianId, weight, temperature, symptoms,
                diagnosis, treatment, notes, true);
    }

    public static Consultation withId(ConsultationId id, AppointmentId appointmentId,
                                      PetId petId, ClientId clientId, UserId veterinarianId,
                                      BigDecimal weight, BigDecimal temperature,
                                      String symptoms, String diagnosis,
                                      String treatment, String notes, boolean active) {
        return new Consultation(id, appointmentId, petId, clientId, veterinarianId,
                weight, temperature, symptoms, diagnosis, treatment, notes, active);
    }

    public ConsultationId getId() {
        return id;
    }

    public AppointmentId getAppointmentId() {
        return appointmentId;
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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        Consultation that = (Consultation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
