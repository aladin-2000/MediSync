package com.project.medisync.modules.acces.service;

import com.project.medisync.modules.acces.entity.BlocagePriorite;
import com.project.medisync.modules.acces.entity.TypeBlocageEnum;

import java.util.List;

/**
 * Interface publique du module Accès.
 * Permet à un médecin de bloquer ou d'accorder un accès prioritaire à un délégué.
 */
public interface BlocagePrioriteService {

    BlocagePriorite definir(String medecinId, String delegueId, TypeBlocageEnum type);

    BlocagePriorite getByMedecinAndDelegue(String medecinId, String delegueId);

    List<BlocagePriorite> getByMedecin(String medecinId, TypeBlocageEnum type);

    void supprimer(String medecinId, String delegueId);

    boolean estBloque(String medecinId, String delegueId);
}
