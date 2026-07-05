package com.project.medisync.modules.reservations.dto;

import com.project.medisync.modules.reservations.entity.AnnuleParEnum;
import com.project.medisync.modules.reservations.entity.RendezVous;
import com.project.medisync.modules.reservations.entity.StatutRendezVousEnum;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente la réponse API contenant les détails d'un rendez-vous.
 */
public record RendezVousResponse(
        /** Identifiant unique du rendez-vous. */
        UUID id,

        /** Identifiant du créneau horaire associé. */
        UUID creneauId,

        /** Identifiant du délégué médical. */
        UUID delegueId,

        /** Identifiant du médecin. */
        UUID medecinId,

        /** Statut actuel du rendez-vous (CONFIRME, ANNULE, REALISE, ABSENT). */
        StatutRendezVousEnum statut,

        /** Qui a annulé le rendez-vous (DELEGUE, MEDECIN), le cas échéant. */
        AnnuleParEnum annulePar,

        /** Le motif d'annulation (renseigné si annulé par le médecin). */
        String motifAnnulation,

        /** Date et heure de création de l'enregistrement. */
        LocalDateTime createdAt
) {
    /**
     * Mappe une entité RendezVous en DTO RendezVousResponse.
     *
     * @param rdv L'entité RendezVous source
     * @return Le DTO correspondant
     */
    public static RendezVousResponse from(RendezVous rdv) {
        return new RendezVousResponse(
                rdv.getId(),
                rdv.getCreneau().getId(),
                rdv.getDelegue().getId(),
                rdv.getMedecin().getId(),
                rdv.getStatut(),
                rdv.getAnnulePar(),
                rdv.getMotifAnnulation(),
                rdv.getCreatedAt()
        );
    }
}
