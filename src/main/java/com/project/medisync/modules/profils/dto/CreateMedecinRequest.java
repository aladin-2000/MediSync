package com.project.medisync.modules.profils.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateMedecinRequest {

    @NotNull(message = "L'identifiant utilisateur est obligatoire.")
    private UUID userId;

    @NotBlank(message = "Le nom est obligatoire.")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire.")
    private String prenom;

    @NotBlank(message = "La spécialité est obligatoire.")
    private String specialite;

    private String adresseCabinet;
    private Double latitude;
    private Double longitude;
    private Float  scoreFiabiliteMin;
}
