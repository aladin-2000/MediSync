package com.project.medisync.modules.disponibilites.service;

import com.project.medisync.modules.disponibilites.entity.Creneau;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface CreneauService {

    /** Création manuelle d'un créneau ponctuel par le médecin. */
    Creneau createManuel(UUID medecinId, LocalDate date, LocalTime heureDebut);

    Creneau getById(UUID id);

    /** Créneaux d'un médecin pour une semaine donnée (lundi → dimanche). */
    List<Creneau> getBySemaine(UUID medecinId, LocalDate lundiDeLaSemaine);

    /** Créneaux disponibles d'un médecin — semaine en cours + semaine suivante.
     *  Utilisé pour afficher les propositions de remplacement. */
    List<Creneau> getDisponiblesPourRemplacement(UUID medecinId);


    /** Marquer un créneau comme réservé. */
    void marquerReserve(UUID id);

    /** Marquer un créneau comme disponible. */
    void marquerDisponible(UUID id);

    /** Appelé par le job @Scheduled chaque vendredi soir. */
  //  void genererCreneauxSemaineProchaine();
}
