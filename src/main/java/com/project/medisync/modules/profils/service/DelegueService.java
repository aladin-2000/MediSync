package com.project.medisync.modules.profils.service;

import com.project.medisync.modules.profils.entity.Delegue;

import java.util.List;
/**
 * Interface publique du service Délégué (module Profils).
 */
public interface DelegueService {

    Delegue create(String userId, String laboratoireId, String nom, String prenom, String telephone);

    Delegue getById(String id);

    /** Récupère le profil délégué associé à un compte utilisateur. */
    Delegue getByUserId(String userId);

    List<Delegue> getAll();

    List<Delegue> getByLaboratoire(String laboratoireId);

    Delegue update(String id, String nom, String prenom, String telephone);

    void updateScoreFiabilite(String id, Float nouveauScore);

    void delete(String id);
}
