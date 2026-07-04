package com.project.medisync.modules.profils.service;

import com.project.medisync.modules.profils.entity.Delegue;

import java.util.List;
import java.util.UUID;

/**
 * Interface publique du service Délégué (module Profils).
 */
public interface DelegueService {

    Delegue create(UUID userId, UUID laboratoireId, String nom, String prenom, String telephone);

    Delegue getById(UUID id);

    List<Delegue> getAll();

    List<Delegue> getByLaboratoire(UUID laboratoireId);

    Delegue update(UUID id, String nom, String prenom, String telephone);

    void updateScoreFiabilite(UUID id, Float nouveauScore);

    void delete(UUID id);
}
