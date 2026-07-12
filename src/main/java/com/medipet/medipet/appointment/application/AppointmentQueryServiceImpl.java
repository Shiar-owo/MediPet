package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.application.AppointmentSummary;
import com.medipet.medipet.appointment.application.port.inbound.AppointmentQueryService;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.port.outbound.ClientNameResolverPort;
import com.medipet.medipet.appointment.domain.port.outbound.PetNameResolverPort;
import com.medipet.medipet.appointment.domain.port.outbound.VeterinarianNameResolverPort;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
class AppointmentQueryServiceImpl implements AppointmentQueryService {

    private final AppointmentRepository appointmentRepository;
    private final PetNameResolverPort petNameResolver;
    private final ClientNameResolverPort clientNameResolver;
    private final VeterinarianNameResolverPort vetNameResolver;

    AppointmentQueryServiceImpl(AppointmentRepository appointmentRepository,
                                PetNameResolverPort petNameResolver,
                                ClientNameResolverPort clientNameResolver,
                                VeterinarianNameResolverPort vetNameResolver) {
        this.appointmentRepository = appointmentRepository;
        this.petNameResolver = petNameResolver;
        this.clientNameResolver = clientNameResolver;
        this.vetNameResolver = vetNameResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentSummary findById(AppointmentId id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment", id.getValue()));
        return toSummary(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentSummary> findByClientId(ClientId clientId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByClientId(clientId, pageable);
        return toPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentSummary> findByPetId(PetId petId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByPetId(petId, pageable);
        return toPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentSummary> findByVeterinarianId(UserId veterinarianId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByVeterinarianId(veterinarianId, pageable);
        return toPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentSummary> findByVeterinarianIdAndClientId(UserId veterinarianId, ClientId clientId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByVeterinarianIdAndClientId(veterinarianId, clientId, pageable);
        return toPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentSummary> findByVeterinarianIdAndPetId(UserId veterinarianId, PetId petId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByVeterinarianIdAndPetId(veterinarianId, petId, pageable);
        return toPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentSummary> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByDateRange(startDate, endDate, pageable);
        return toPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AppointmentSummary> findAll(Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAll(pageable);
        return toPageResponse(page);
    }

    private PageResponse<AppointmentSummary> toPageResponse(Page<Appointment> page) {
        List<AppointmentSummary> summaries = toSummaries(page.getContent());
        return new PageResponse<>(summaries, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    private List<AppointmentSummary> toSummaries(List<Appointment> appointments) {
        Map<PetId, String> petNames = petNameResolver.resolveNames(
                appointments.stream().map(Appointment::getPetId).distinct().toList());
        Map<ClientId, String> clientNames = clientNameResolver.resolveNames(
                appointments.stream().map(Appointment::getClientId).distinct().toList());
        Map<UserId, String> vetNames = vetNameResolver.resolveNames(
                appointments.stream().map(Appointment::getVeterinarianId).distinct().toList());

        return appointments.stream()
                .map(a -> AppointmentSummary.from(a, petNames, clientNames, vetNames))
                .toList();
    }

    private AppointmentSummary toSummary(Appointment appointment) {
        return toSummaries(List.of(appointment)).get(0);
    }
}
