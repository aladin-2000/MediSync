package com.project.medisync.modules.analytics.controller;

import com.project.medisync.modules.analytics.dto.DashboardCountsResponse;
import com.project.medisync.modules.analytics.dto.StatsVisitesDelegueResponse;
import com.project.medisync.modules.analytics.dto.StatsVisitesLaboResponse;
import com.project.medisync.modules.analytics.service.AdminStatsService;
import com.project.medisync.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardCountsResponse>> dashboardCounts() {
        return ResponseEntity.ok(ApiResponse.ok(adminStatsService.getDashboardCounts()));
    }

    @GetMapping("/visites/par-laboratoire")
    public ResponseEntity<ApiResponse<List<StatsVisitesLaboResponse>>> visitesParLaboratoire(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        return ResponseEntity.ok(ApiResponse.ok(adminStatsService.getVisitesParLaboratoire(mois, annee)));
    }

    @GetMapping("/visites/par-delegue")
    public ResponseEntity<ApiResponse<List<StatsVisitesDelegueResponse>>> visitesParDelegue(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        return ResponseEntity.ok(ApiResponse.ok(adminStatsService.getVisitesParDelegue(mois, annee)));
    }

    @GetMapping("/visites/par-delegue/laboratoire/{laboId}")
    public ResponseEntity<ApiResponse<List<StatsVisitesDelegueResponse>>> visitesParDelegueForLabo(
            @PathVariable String laboId,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        return ResponseEntity.ok(ApiResponse.ok(
                adminStatsService.getVisitesParDelegueForLaboratoire(laboId, mois, annee)));
    }
}
