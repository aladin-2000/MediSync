package com.project.medisync.modules.profils.dto;

import com.project.medisync.modules.profils.entity.Medecin;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MedecinResponse {

    private String          id;
    private String        userId;
    private String        nom;
    private String        prenom;
    private String        specialite;
    private String        adresseCabinet;
    private Double        latitude;
    private Double        longitude;
    private String        photoUrl;
    private Float         scoreFiabiliteMin;
    private LocalDateTime createdAt;

    public static MedecinResponse from(Medecin medecin) {
        return MedecinResponse.builder()
                .id(medecin.getId())
                .userId(medecin.getUser().getId())
                .nom(medecin.getNom())
                .prenom(medecin.getPrenom())
                .specialite(medecin.getSpecialite())
                .adresseCabinet(medecin.getAdresseCabinet())
                .latitude(medecin.getLatitude())
                .longitude(medecin.getLongitude())
                .photoUrl(medecin.getPhotoUrl())
                .scoreFiabiliteMin(medecin.getScoreFiabiliteMin())
                .createdAt(medecin.getCreatedAt())
                .build();
    }
}
