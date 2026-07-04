package com.project.medisync.modules.reservations.service;

import com.project.medisync.modules.reservations.entity.PropositionRemplacement;

import java.util.List;
import java.util.UUID;

/**
 * Interface publique du service PropositionRemplacement.
 */
public interface PropositionRemplacementService {

    PropositionRemplacement getById(UUID id);

    List<PropositionRemplacement> getEnAttenteByDelegue(UUID delegueId);

    /** Le délégué accepte un créneau de remplacement. */
    PropositionRemplacement accepter(UUID propositionId, UUID nouveauCreneauId);

    /** Job planifié — expire les propositions non acceptées dans le délai imparti. */
    void expirePropositionsNonAcceptees();
}
