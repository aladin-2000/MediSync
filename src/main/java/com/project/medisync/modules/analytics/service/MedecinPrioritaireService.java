package com.project.medisync.modules.analytics.service;

import com.project.medisync.modules.analytics.entity.MedecinPrioritaire;

import java.util.List;

/**
 * Interface publique du module Analytics.
 */
public interface MedecinPrioritaireService {

    MedecinPrioritaire ajouter(String laboratoireId, String medecinId);

    List<MedecinPrioritaire> getByLaboratoire(String laboratoireId);

    void retirer(String laboratoireId, String medecinId);

    boolean estPrioritaire(String laboratoireId, String medecinId);
}
