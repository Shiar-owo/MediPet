package com.medipet.medipet.dashboard.infrastructure.inbound.rest.dto.response;

import com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.response.AppointmentResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Dashboard statistics response")
public record DashboardStatsResponse(

        @Schema(description = "Total active clients")
        long totalClients,

        @Schema(description = "Total active pets")
        long totalPets,

        @Schema(description = "Appointments scheduled for today")
        long appointmentsToday,

        @Schema(description = "Appointments scheduled this month")
        long appointmentsThisMonth,

        @Schema(description = "Upcoming appointments in the next 7 days")
        List<AppointmentResponse> upcomingAppointments
) {}
