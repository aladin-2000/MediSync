package com.project.medisync.modules.profils.dto;

import com.project.medisync.modules.profils.entity.Delegue;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DelegueResponse {

    private UUID          id;
    private UUID          userId;
    private UUID          laboratoireId;
    private String        nom;
    private String        prenom;
    private String        telephone;
    private String        photoUrl;
    private Float         scoreFiabilite;
    private LocalDateTime createdAt;

    public static DelegueResponse from(Delegue delegue) {
        return DelegueResponse.builder()
                .id(delegue.getId())
                .userId(delegue.getUser().getId())
                .laboratoireId(delegue.getLaboratoire().getId())
                .nom(delegue.getNom())
                .prenom(delegue.getPrenom())
                .telephone(delegue.getTelephone())
                .photoUrl(delegue.getPhotoUrl())
                .scoreFiabilite(delegue.getScoreFiabilite())
                .createdAt(delegue.getCreatedAt())
                .build();
    }
}
