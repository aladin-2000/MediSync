package com.project.medisync.modules.profils.dto;

import com.project.medisync.modules.profils.entity.Laboratoire;
import com.project.medisync.modules.profils.entity.StatutAbonnementEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class LaboratoireResponse {

    private UUID                 id;
    private UUID                 userId;
    private String               nom;
    private String               adresse;
    private StatutAbonnementEnum statutAbonnement;
    private LocalDate            dateDebutAbonnement;
    private LocalDate            dateFinAbonnement;
    private UUID                 dernierPaiementId;
    private LocalDateTime        createdAt;

    public static LaboratoireResponse from(Laboratoire labo) {
        return LaboratoireResponse.builder()
                .id(labo.getId())
                .userId(labo.getUser().getId())
                .nom(labo.getNom())
                .adresse(labo.getAdresse())
                .statutAbonnement(labo.getStatutAbonnement())
                .dateDebutAbonnement(labo.getDateDebutAbonnement())
                .dateFinAbonnement(labo.getDateFinAbonnement())
                .dernierPaiementId(labo.getDernierPaiementId())
                .createdAt(labo.getCreatedAt())
                .build();
    }
}
