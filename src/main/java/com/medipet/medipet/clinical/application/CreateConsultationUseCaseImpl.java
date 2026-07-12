package com.medipet.medipet.clinical.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.application.port.inbound.CreateConsultationUseCase;
import com.medipet.medipet.clinical.domain.port.outbound.ConsultationRepository;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CreateConsultationUseCaseImpl implements CreateConsultationUseCase {

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;

    CreateConsultationUseCaseImpl(ConsultationRepository consultationRepository,
                                  AppointmentRepository appointmentRepository) {
        this.consultationRepository = consultationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public Consultation execute(Command command) {
        Appointment appointment = appointmentRepository.findById(AppointmentId.from(command.appointmentId()))
                .orElseThrow(() -> new NotFoundException("Appointment", command.appointmentId()));

        if (appointment.getStatus() != AppointmentStatus.COMPLETADA) {
            throw new BadRequestException("Consultation can only be created for a completed appointment");
        }

        consultationRepository.findByAppointmentId(AppointmentId.from(command.appointmentId()))
                .ifPresent(c -> {
                    throw new BadRequestException("Consultation already exists for this appointment");
                });

        Consultation consultation = Consultation.create(
                AppointmentId.from(command.appointmentId()),
                PetId.from(command.petId()),
                ClientId.from(command.clientId()),
                UserId.from(command.veterinarianId()),
                command.weight(),
                command.temperature(),
                command.symptoms(),
                command.diagnosis(),
                command.treatment(),
                command.notes()
        );

        return consultationRepository.save(consultation);
    }
}
