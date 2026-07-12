package com.medipet.medipet.dashboard.infrastructure.inbound.rest;

import com.medipet.medipet.dashboard.application.DashboardStats;
import com.medipet.medipet.dashboard.application.port.inbound.DashboardQueryService;
import com.medipet.medipet.dashboard.infrastructure.inbound.rest.dto.response.DashboardStatsResponse;
import com.medipet.medipet.dashboard.infrastructure.inbound.rest.mapper.DashboardMapper;
import com.medipet.medipet.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics")
class DashboardController {

    private final DashboardQueryService dashboardQueryService;

    DashboardController(DashboardQueryService dashboardQueryService) {
        this.dashboardQueryService = dashboardQueryService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        DashboardStats stats = dashboardQueryService.getStats();
        DashboardStatsResponse response = DashboardMapper.toResponse(stats);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
