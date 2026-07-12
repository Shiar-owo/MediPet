package com.medipet.medipet.dashboard.application;

import com.medipet.medipet.appointment.application.AppointmentSummary;

import java.util.List;

public record DashboardStats(
        long totalClients,
        long totalPets,
        long appointmentsToday,
        long appointmentsThisMonth,
        List<AppointmentSummary> upcomingAppointments
) {}
