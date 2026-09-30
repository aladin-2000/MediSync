package com.project.medisync.modules.analytics.dto;

public record DashboardCountsResponse(
        long totalMedecins,
        long totalDelegues,
        long totalLaboratoires
) {}
