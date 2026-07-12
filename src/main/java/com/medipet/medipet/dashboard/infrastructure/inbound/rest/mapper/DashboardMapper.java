package com.medipet.medipet.dashboard.infrastructure.inbound.rest.mapper;

import com.medipet.medipet.appointment.infrastructure.inbound.rest.mapper.AppointmentMapper;
import com.medipet.medipet.dashboard.application.DashboardStats;
import com.medipet.medipet.dashboard.infrastructure.inbound.rest.dto.response.DashboardStatsResponse;

public class DashboardMapper {

    private DashboardMapper() {}

    public static DashboardStatsResponse toResponse(DashboardStats stats) {
        return new DashboardStatsResponse(
                stats.totalClients(),
                stats.totalPets(),
                stats.appointmentsToday(),
                stats.appointmentsThisMonth(),
                stats.upcomingAppointments().stream()
                        .map(AppointmentMapper::toResponse)
                        .toList()
        );
    }
}
