package com.medipet.medipet.dashboard.application.port.inbound;

import com.medipet.medipet.dashboard.application.DashboardStats;

public interface DashboardQueryService {
    DashboardStats getStats();
}
