package com.project.medisync.modules.reservations.dto;

import com.project.medisync.modules.reservations.entity.PropositionRemplacement;
import com.project.medisync.modules.reservations.entity.StatutPropositionEnum;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Représente la réponse API contenant les détails d'une proposition de remplacement.
 */
public record PropositionRemplacementResponse(
        /** Identifiant unique de la proposition de remplacement. */
        UUID id,

        /** Identifiant du rendez-vous d'origine qui a été annulé par le médecin. */
        UUID rendezVousAnnuleId,

        /** Identifiant du nouveau rendez-vous accepté, ou null si non encore accepté ou expirée. */
        UUID rendezVousNouveauId,

        /** Identifiant du délégué médical concerné. */
        UUID delegueId,

        /** Identifiant du médecin concerné. */
        UUID medecinId,

        /** Statut de la proposition (EN_ATTENTE, ACCEPTE, EXPIRE). */
        StatutPropositionEnum statut,

        /** Date et heure de création de la proposition. */
        LocalDateTime createdAt
) {
    /**
     * Mappe une entité PropositionRemplacement en DTO PropositionRemplacementResponse.
     *
     * @param prop L'entité PropositionRemplacement source
     * @return Le DTO correspondant
     */
    public static PropositionRemplacementResponse from(PropositionRemplacement prop) {
        return new PropositionRemplacementResponse(
                prop.getId(),
                prop.getRendezVousAnnule().getId(),
                prop.getRendezVousNouveau() != null ? prop.getRendezVousNouveau().getId() : null,
                prop.getDelegue().getId(),
                prop.getMedecin().getId(),
                prop.getStatut(),
                prop.getCreatedAt()
        );
    }
}
