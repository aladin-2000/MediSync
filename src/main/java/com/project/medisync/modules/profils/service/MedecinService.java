package com.project.medisync.modules.profils.service;

import com.project.medisync.modules.profils.entity.Medecin;
import com.project.medisync.modules.profils.entity.SpecialiteEnum;

import java.util.List;

/**
 * Interface publique du service Médecin (module Profils).
 * Les autres modules utilisent cette interface pour accéder aux données médecin.
 */
public interface MedecinService {

    Medecin create(String userId, String nom, String prenom, SpecialiteEnum specialite,
                   String adresseCabinet, String telephone, Double latitude, Double longitude, Float scoreFiabiliteMin);

    /**
     * Crée en une seule fois le compte utilisateur (email + mot de passe, rôle MEDECIN)
     * et le profil médecin associé. Utilisé par l'admin pour créer rapidement des médecins.
     */
    Medecin creerMedecinComplet(String email, String password, String nom, String prenom, SpecialiteEnum specialite,
                                 String adresseCabinet, String telephone, Double latitude, Double longitude, Float scoreFiabiliteMin);

    Medecin getById(String id);

    /** Récupère le profil médecin associé à un compte utilisateur. */
    Medecin getByUserId(String userId);

    List<Medecin> getAll();

    /** Récupère plusieurs médecins par leurs ids (une seule requête). */
    List<Medecin> getByIds(List<String> ids);

    /**
     * Recherche par nom (partiel) et spécialités (une ou plusieurs, optionnel) parmi
     * une liste d'ids donnée. specialites == null ou vide -> pas de filtre par spécialité.
     */
    List<Medecin> searchByIdsNomSpecialites(List<String> ids, String nom, List<SpecialiteEnum> specialites);

    List<Medecin> getBySpecialite(SpecialiteEnum specialite);

    Medecin update(String id, String nom, String prenom, SpecialiteEnum specialite,
                   String adresseCabinet, String telephone, Double latitude, Double longitude, Float scoreFiabiliteMin);

    void delete(String id);
}
