package com.project.medisync.modules.disponibilites.service;

import com.project.medisync.modules.disponibilites.entity.DisponibiliteHebdomadaire;
import com.project.medisync.modules.disponibilites.entity.JourSemaineEnum;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface DisponibiliteHebdomadaireService {

    DisponibiliteHebdomadaire create(UUID medecinId, JourSemaineEnum jour,
                                     LocalTime heureDebut, LocalTime heureFin);

    DisponibiliteHebdomadaire getById(UUID id);

    List<DisponibiliteHebdomadaire> getByMedecin(UUID medecinId);

    List<DisponibiliteHebdomadaire> getActivesByMedecin(UUID medecinId);

    DisponibiliteHebdomadaire update(UUID id, JourSemaineEnum jour,
                                     LocalTime heureDebut, LocalTime heureFin);

    void toggleActive(UUID id, boolean isActive);

    void delete(UUID id);
}
