package com.project.medisync.modules.profils.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class CreateDelegueRequest {

    @NotNull(message = "L'identifiant utilisateur est obligatoire.")
    private String userId;

    @NotNull(message = "L'identifiant du laboratoire est obligatoire.")
    private String laboratoireId;

    @NotBlank(message = "Le nom est obligatoire.")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire.")
    private String prenom;

    private String telephone;
}
