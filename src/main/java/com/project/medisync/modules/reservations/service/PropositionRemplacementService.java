package com.project.medisync.modules.reservations.service;

import com.project.medisync.modules.reservations.entity.PropositionRemplacement;

import java.util.List;

/**
 * Interface publique du service PropositionRemplacement.
 */
public interface PropositionRemplacementService {

    PropositionRemplacement getById(String id);

    List<PropositionRemplacement> getEnAttenteByDelegue(String delegueId);

    /** Le délégué accepte un créneau de remplacement. */
    PropositionRemplacement accepter(String propositionId, String nouveauCreneauId);

    /** Job planifié — expire les propositions non acceptées dans le délai imparti. */
    void expirePropositionsNonAcceptees();
}
