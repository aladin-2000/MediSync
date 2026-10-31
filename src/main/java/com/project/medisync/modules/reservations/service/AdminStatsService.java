package com.project.medisync.modules.reservations.service;

import com.project.medisync.modules.reservations.dto.DashboardCountsResponse;
import com.project.medisync.modules.reservations.dto.StatsVisitesDelegueResponse;
import com.project.medisync.modules.reservations.dto.StatsVisitesLaboResponse;

import java.util.List;

public interface AdminStatsService {

    DashboardCountsResponse getDashboardCounts();

    List<StatsVisitesLaboResponse> getVisitesParLaboratoire(Integer mois, Integer annee);

    List<StatsVisitesDelegueResponse> getVisitesParDelegue(Integer mois, Integer annee);

    List<StatsVisitesDelegueResponse> getVisitesParDelegueForLabo(String laboratoireId, Integer mois, Integer annee);
}
