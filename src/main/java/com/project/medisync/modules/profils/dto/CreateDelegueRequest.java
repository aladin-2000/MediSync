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
public class CreateDelegueRequest {

    @NotNull(message = "L'identifiant utilisateur est obligatoire.")
    private UUID userId;

    @NotNull(message = "L'identifiant du laboratoire est obligatoire.")
    private UUID laboratoireId;

    @NotBlank(message = "Le nom est obligatoire.")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire.")
    private String prenom;

    private String telephone;
}
