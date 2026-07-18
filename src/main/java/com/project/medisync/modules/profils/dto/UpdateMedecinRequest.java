package com.project.medisync.modules.profils.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMedecinRequest {

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
