package com.project.medisync.modules.analytics.dto;

public record StatsVisitesLaboResponse(
        String laboratoireId,
        String laboratoireNom,
        long nombreVisites
) {}
