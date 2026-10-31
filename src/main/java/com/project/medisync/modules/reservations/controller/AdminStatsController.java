package com.project.medisync.modules.reservations.controller;

import com.project.medisync.modules.reservations.dto.DashboardCountsResponse;
import com.project.medisync.modules.reservations.dto.StatsVisitesDelegueResponse;
import com.project.medisync.modules.reservations.dto.StatsVisitesLaboResponse;
import com.project.medisync.modules.reservations.service.AdminStatsService;
import com.project.medisync.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardCountsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok("Compteurs du dashboard.", adminStatsService.getDashboardCounts()));
    }

    @GetMapping("/visites/par-laboratoire")
    public ResponseEntity<ApiResponse<List<StatsVisitesLaboResponse>>> getVisitesParLaboratoire(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        return ResponseEntity.ok(ApiResponse.ok("Visites par laboratoire.", adminStatsService.getVisitesParLaboratoire(mois, annee)));
    }

    @GetMapping("/visites/par-delegue")
    public ResponseEntity<ApiResponse<List<StatsVisitesDelegueResponse>>> getVisitesParDelegue(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        return ResponseEntity.ok(ApiResponse.ok("Visites par délégué.", adminStatsService.getVisitesParDelegue(mois, annee)));
    }

    @GetMapping("/visites/par-delegue/laboratoire/{laboId}")
    public ResponseEntity<ApiResponse<List<StatsVisitesDelegueResponse>>> getVisitesParDelegueForLabo(
            @PathVariable String laboId,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        return ResponseEntity.ok(ApiResponse.ok("Visites par délégué pour le laboratoire.", adminStatsService.getVisitesParDelegueForLabo(laboId, mois, annee)));
    }
}
