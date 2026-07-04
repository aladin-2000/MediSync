package com.project.medisync.modules.analytics.service;

import com.project.medisync.modules.analytics.entity.MedecinPrioritaire;

import java.util.List;
import java.util.UUID;

/**
 * Interface publique du module Analytics.
 */
public interface MedecinPrioritaireService {

    MedecinPrioritaire ajouter(UUID laboratoireId, UUID medecinId);

    List<MedecinPrioritaire> getByLaboratoire(UUID laboratoireId);

    void retirer(UUID laboratoireId, UUID medecinId);

    boolean estPrioritaire(UUID laboratoireId, UUID medecinId);
}
