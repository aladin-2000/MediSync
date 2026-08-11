package com.project.medisync.modules.profils.service;

import com.project.medisync.modules.profils.entity.Laboratoire;
import com.project.medisync.modules.profils.entity.StatutAbonnementEnum;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface publique du service Laboratoire (module Profils).
 */
public interface LaboratoireService {

    Laboratoire create(String userId, String nom, String adresse, String telephone,
                       StatutAbonnementEnum statut, LocalDate dateDebut, LocalDate dateFin);

    Laboratoire getById(String id);

    /** Récupère le laboratoire associé à un compte utilisateur (rôle LABO). */
    Laboratoire getByUserId(String userId);

    List<Laboratoire> getAll();

    Laboratoire update(String id, String nom, String adresse, String telephone,
                       StatutAbonnementEnum statut, LocalDate dateDebut, LocalDate dateFin);

    void updateDernierPaiement(String laboratoireId, String paiementId);

    void delete(String id);
}
