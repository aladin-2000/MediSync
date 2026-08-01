package com.project.medisync.modules.reservations.service;

import com.project.medisync.modules.reservations.entity.RendezVous;

import java.time.LocalDate;
import java.util.List;

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

    RendezVous reserver(String creneauId, String delegueId, String medecinId);

    RendezVous getById(String id);

    List<RendezVous> getByDelegue(String delegueId);

    /** RDV d'un délégué pour un jour donné. */
    List<RendezVous> getByDelegueEtJour(String delegueId, LocalDate date);

    /** RDV d'un délégué pour la semaine (lundi → dimanche) contenant lundiDeLaSemaine. */
    List<RendezVous> getByDelegueEtSemaine(String delegueId, LocalDate lundiDeLaSemaine);

    List<RendezVous> getByMedecin(String medecinId);

    /** RDV d'un médecin pour la semaine (lundi → dimanche) contenant lundiDeLaSemaine. */
    List<RendezVous> getByMedecinEtSemaine(String medecinId, LocalDate lundiDeLaSemaine);

    /** Annulation par le délégué. */
    RendezVous annulerParDelegue(String rendezVousId);

    /** Annulation par le médecin — motif obligatoire, déclenche une PropositionRemplacement. */
    RendezVous annulerParMedecin(String rendezVousId, String motifAnnulation);

    /** Marque un rendez-vous comme réalisé. */
    RendezVous marquerRealise(String rendezVousId);

    /** Marque le délégué comme absent. */
    RendezVous marquerAbsent(String rendezVousId);
}
