package com.project.medisync.modules.disponibilites.service;

import com.project.medisync.modules.disponibilites.entity.Creneau;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CreneauService {

    /** Création manuelle d'un créneau ponctuel par le médecin. */
    Creneau createCreneau(String medecinId, LocalDate date, LocalTime heureDebut);

    /**
     * Génère en masse tous les créneaux de 15 minutes d'un médecin,
     * pour chaque jour entre dateDebut et dateFin (inclus),
     * entre heureDebut (inclus) et heureFin (exclu).
     * Les créneaux déjà existants sont ignorés (pas de doublon).
     */
    List<Creneau> ajouterUnePlageDeCreneaux(String medecinId, LocalDate dateDebut, LocalDate dateFin,
                                             LocalTime heureDebut, LocalTime heureFin);

    /**
     * Supprime en masse les créneaux DISPONIBLES d'un médecin sur une plage de dates/heures.
     * Les créneaux déjà réservés ne sont pas touchés.
     * @return le nombre de créneaux supprimés.
     */
    int supprimerUnePlageDeCreneaux(String medecinId, LocalDate dateDebut, LocalDate dateFin,
                                     LocalTime heureDebut, LocalTime heureFin);

    Creneau getById(String id);

    /** Créneaux d'un médecin pour une semaine donnée (lundi → dimanche). */
    List<Creneau> getBySemaine(String medecinId, LocalDate lundiDeLaSemaine);

    /** Créneaux disponibles d'un médecin — semaine en cours + semaine suivante.
     *  Utilisé pour afficher les propositions de remplacement. */
    List<Creneau> getDisponiblesPourRemplacement(String medecinId);


    /** Marquer un créneau comme réservé. */
    void marquerReserve(String id);

    /** Marquer un créneau comme disponible. */
    void marquerDisponible(String id);

    List<Creneau> getAllCreneaux();


    /** Appelé par le job @Scheduled chaque vendredi soir. */
  //  void genererCreneauxSemaineProchaine();
}
