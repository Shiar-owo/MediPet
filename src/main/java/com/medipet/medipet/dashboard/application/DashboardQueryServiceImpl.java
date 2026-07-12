package com.medipet.medipet.dashboard.application;

import com.medipet.medipet.appointment.application.AppointmentSummary;
import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.port.outbound.ClientNameResolverPort;
import com.medipet.medipet.appointment.domain.port.outbound.PetNameResolverPort;
import com.medipet.medipet.appointment.domain.port.outbound.VeterinarianNameResolverPort;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.dashboard.application.port.inbound.DashboardQueryService;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class DashboardQueryServiceImpl implements DashboardQueryService {

    private static final int UPCOMING_LIMIT = 10;

    private final ClientRepository clientRepository;
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;
    private final PetNameResolverPort petNameResolver;
    private final ClientNameResolverPort clientNameResolver;
    private final VeterinarianNameResolverPort vetNameResolver;

    DashboardQueryServiceImpl(ClientRepository clientRepository,
                              PetRepository petRepository,
                              AppointmentRepository appointmentRepository,
                              PetNameResolverPort petNameResolver,
                              ClientNameResolverPort clientNameResolver,
                              VeterinarianNameResolverPort vetNameResolver) {
        this.clientRepository = clientRepository;
        this.petRepository = petRepository;
        this.appointmentRepository = appointmentRepository;
        this.petNameResolver = petNameResolver;
        this.clientNameResolver = clientNameResolver;
        this.vetNameResolver = vetNameResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        LocalDate endDate = today.plusDays(7);

        long totalClients = clientRepository.findAll(PageRequest.of(0, 1)).getTotalElements();
        long totalPets = petRepository.findAll(PageRequest.of(0, 1)).getTotalElements();
        long appointmentsToday = appointmentRepository
                .findByDateRange(today, today, PageRequest.of(0, 1))
                .getTotalElements();
        long appointmentsThisMonth = appointmentRepository
                .findByDateRange(startOfMonth, endOfMonth, PageRequest.of(0, 1))
                .getTotalElements();

        List<Appointment> upcomingAppts = appointmentRepository
                .findByDateRange(today, endDate, PageRequest.of(0, UPCOMING_LIMIT))
                .getContent()
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDIENTE
                        || a.getStatus() == AppointmentStatus.CONFIRMADA)
                .toList();

        List<AppointmentSummary> summaries = resolveNames(upcomingAppts);

        return new DashboardStats(totalClients, totalPets, appointmentsToday,
                appointmentsThisMonth, summaries);
    }

    private List<AppointmentSummary> resolveNames(List<Appointment> appointments) {
        if (appointments.isEmpty()) {
            return List.of();
        }

        Set<PetId> petIds = appointments.stream()
                .map(Appointment::getPetId).collect(Collectors.toSet());
        Set<ClientId> clientIds = appointments.stream()
                .map(Appointment::getClientId).collect(Collectors.toSet());
        Set<UserId> vetIds = appointments.stream()
                .map(Appointment::getVeterinarianId).collect(Collectors.toSet());

        Map<PetId, String> petNames = petNameResolver.resolveNames(petIds);
        Map<ClientId, String> clientNames = clientNameResolver.resolveNames(clientIds);
        Map<UserId, String> vetNames = vetNameResolver.resolveNames(vetIds);

        return appointments.stream()
                .map(a -> AppointmentSummary.from(a, petNames, clientNames, vetNames))
                .toList();
    }
}
