package com.project.medisync.modules.disponibilites.dto;

import com.project.medisync.modules.disponibilites.entity.DisponibiliteHebdomadaire;
import com.project.medisync.modules.disponibilites.entity.JourSemaineEnum;

import java.time.LocalTime;
import java.util.UUID;

public record DisponibiliteHebdomadaireResponse(
        UUID            id,
        UUID            medecinId,
        JourSemaineEnum jourSemaine,
        LocalTime       heureDebut,
        LocalTime       heureFin,
        Boolean         isActive
) {
    public static DisponibiliteHebdomadaireResponse from(DisponibiliteHebdomadaire d) {
        return new DisponibiliteHebdomadaireResponse(
                d.getId(),
                d.getMedecinId(),
                d.getJourSemaine(),
                d.getHeureDebut(),
                d.getHeureFin(),
                d.getIsActive()
        );
    }
}
