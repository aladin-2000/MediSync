package com.project.medisync.modules.reservations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsVisitesLaboResponse {
    private String laboratoireId;
    private String laboratoireNom;
    private long nombreVisites;
}
