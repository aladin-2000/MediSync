package com.project.medisync.modules.disponibilites.dto;

import com.project.medisync.modules.disponibilites.entity.JourSemaineEnum;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record DisponibiliteHebdomadaireRequest(

        @NotNull(message = "Le jour de la semaine est obligatoire.")
        JourSemaineEnum jourSemaine,

        @NotNull(message = "L'heure de début est obligatoire.")
        LocalTime heureDebut,

        @NotNull(message = "L'heure de fin est obligatoire.")
        LocalTime heureFin
) {}
