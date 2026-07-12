package com.medipet.medipet.clinical.application;

import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.application.port.inbound.ConsultationQueryService;
import com.medipet.medipet.clinical.domain.port.outbound.ConsultationRepository;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ConsultationQueryServiceImpl implements ConsultationQueryService {

    private final ConsultationRepository consultationRepository;
    private final PetRepository petRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    ConsultationQueryServiceImpl(ConsultationRepository consultationRepository,
                                 PetRepository petRepository,
                                 ClientRepository clientRepository,
                                 UserRepository userRepository) {
        this.consultationRepository = consultationRepository;
        this.petRepository = petRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultationSummary getById(ConsultationId id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Consultation", id.getValue()));
        return toSummary(consultation);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultationSummary getByAppointmentId(AppointmentId appointmentId) {
        Consultation consultation = consultationRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new NotFoundException("Consultation for appointment", appointmentId.getValue()));
        return toSummary(consultation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConsultationSummary> getByPetId(PetId petId, Pageable pageable) {
        return consultationRepository.findByPetId(petId, pageable)
                .map(this::toSummary);
    }

    private ConsultationSummary toSummary(Consultation consultation) {
        String petName = petRepository.findById(consultation.getPetId())
                .map(Pet::getName)
                .orElse("Unknown");
        String clientName = clientRepository.findById(consultation.getClientId())
                .map(Client::getName)
                .orElse("Unknown");
        String vetName = userRepository.findById(consultation.getVeterinarianId())
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("Unknown");

        return new ConsultationSummary(
                consultation.getId().getValue(),
                consultation.getAppointmentId().getValue(),
                consultation.getPetId().getValue(),
                petName,
                consultation.getClientId().getValue(),
                clientName,
                consultation.getVeterinarianId().getValue(),
                vetName,
                consultation.getWeight(),
                consultation.getTemperature(),
                consultation.getSymptoms(),
                consultation.getDiagnosis(),
                consultation.getTreatment(),
                consultation.getNotes(),
                consultation.isActive(),
                null,
                null
        );
    }
}
