package com.project.medisync.modules.reservations.service;

import com.project.medisync.modules.reservations.entity.RendezVous;

import java.util.List;
import java.util.UUID;

/**
 * Interface publique du module Réservations.
 *
 * <p><b>Règles métier :</b>
 * <ul>
 *   <li>Un créneau ne peut être réservé qu'une seule fois.</li>
 *   <li>Un délégué ne peut pas avoir deux rendez-vous au même moment.</li>
 *   <li>Si le médecin annule, {@code motifAnnulation} est obligatoire
 *       et une PropositionRemplacement est créée automatiquement.</li>
 * </ul>
 * </p>
 */
public interface RendezVousService {

    RendezVous reserver(UUID creneauId, UUID delegueId, UUID medecinId);

    RendezVous getById(UUID id);

    List<RendezVous> getByDelegue(UUID delegueId);

    List<RendezVous> getByMedecin(UUID medecinId);

    /** Annulation par le délégué. */
    RendezVous annulerParDelegue(UUID rendezVousId);

    /** Annulation par le médecin — motif obligatoire, déclenche une PropositionRemplacement. */
    RendezVous annulerParMedecin(UUID rendezVousId, String motifAnnulation);

    /** Marque un rendez-vous comme réalisé. */
    RendezVous marquerRealise(UUID rendezVousId);

    /** Marque le délégué comme absent. */
    RendezVous marquerAbsent(UUID rendezVousId);
}
