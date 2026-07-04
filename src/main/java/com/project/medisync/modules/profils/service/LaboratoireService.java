package com.project.medisync.modules.profils.service;

import com.project.medisync.modules.profils.entity.Laboratoire;
import com.project.medisync.modules.profils.entity.StatutAbonnementEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Interface publique du service Laboratoire (module Profils).
 */
public interface LaboratoireService {

    Laboratoire create(UUID userId, String nom, String adresse,
                       StatutAbonnementEnum statut, LocalDate dateDebut, LocalDate dateFin);

    Laboratoire getById(UUID id);

    List<Laboratoire> getAll();

    Laboratoire update(UUID id, String nom, String adresse,
                       StatutAbonnementEnum statut, LocalDate dateDebut, LocalDate dateFin);

    void updateDernierPaiement(UUID laboratoireId, UUID paiementId);

    void delete(UUID id);
}
