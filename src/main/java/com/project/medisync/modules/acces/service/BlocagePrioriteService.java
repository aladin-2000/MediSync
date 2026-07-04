package com.project.medisync.modules.acces.service;

import com.project.medisync.modules.acces.entity.BlocagePriorite;
import com.project.medisync.modules.acces.entity.TypeBlocageEnum;

import java.util.List;
import java.util.UUID;

/**
 * Interface publique du module Accès.
 * Permet à un médecin de bloquer ou d'accorder un accès prioritaire à un délégué.
 */
public interface BlocagePrioriteService {

    BlocagePriorite definir(UUID medecinId, UUID delegueId, TypeBlocageEnum type);

    BlocagePriorite getByMedecinAndDelegue(UUID medecinId, UUID delegueId);

    List<BlocagePriorite> getByMedecin(UUID medecinId, TypeBlocageEnum type);

    void supprimer(UUID medecinId, UUID delegueId);

    boolean estBloque(UUID medecinId, UUID delegueId);
}
