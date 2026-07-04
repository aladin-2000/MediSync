package com.project.medisync.modules.profils.service;

import com.project.medisync.modules.profils.entity.Medecin;

import java.util.List;
import java.util.UUID;

/**
 * Interface publique du service Médecin (module Profils).
 * Les autres modules utilisent cette interface pour accéder aux données médecin.
 */
public interface MedecinService {

    Medecin create(UUID userId, String nom, String prenom, String specialite,
                   String adresseCabinet, Double latitude, Double longitude, Float scoreFiabiliteMin);

    Medecin getById(UUID id);

    List<Medecin> getAll();

    List<Medecin> getBySpecialite(String specialite);

    Medecin update(UUID id, String nom, String prenom, String specialite,
                   String adresseCabinet, Double latitude, Double longitude, Float scoreFiabiliteMin);

    void delete(UUID id);
}
