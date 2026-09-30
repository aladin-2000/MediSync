package com.project.medisync.modules.analytics.dto;

public record StatsVisitesDelegueResponse(
        String delegueId,
        String delegueNom,
        String deleguePrenom,
        String laboratoireId,
        String laboratoireNom,
        long nombreVisites
) {}
