package com.project.medisync.modules.analytics.service;

import com.project.medisync.modules.analytics.dto.DashboardCountsResponse;
import com.project.medisync.modules.analytics.dto.StatsVisitesDelegueResponse;
import com.project.medisync.modules.analytics.dto.StatsVisitesLaboResponse;

import java.util.List;

public interface AdminStatsService {

    DashboardCountsResponse getDashboardCounts();

    List<StatsVisitesLaboResponse> getVisitesParLaboratoire(Integer mois, Integer annee);

    List<StatsVisitesDelegueResponse> getVisitesParDelegue(Integer mois, Integer annee);

    List<StatsVisitesDelegueResponse> getVisitesParDelegueForLaboratoire(String laboratoireId, Integer mois, Integer annee);
}
