package com.project.medisync.modules.disponibilites.service;

import com.project.medisync.modules.disponibilites.entity.Creneau;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CreneauService {

    /** Création manuelle d'un créneau ponctuel par le médecin. */
    Creneau createCreneau(String medecinId, LocalDate date, LocalTime heureDebut);

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

    /** Appelé par le job @Scheduled chaque vendredi soir. */
  //  void genererCreneauxSemaineProchaine();
}
