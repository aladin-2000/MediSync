package com.project.medisync.modules.disponibilites.service.impl;

import com.project.medisync.modules.disponibilites.entity.DisponibiliteHebdomadaire;
import com.project.medisync.modules.disponibilites.entity.JourSemaineEnum;
import com.project.medisync.modules.disponibilites.repository.CreneauRepository;
import com.project.medisync.modules.disponibilites.repository.DisponibiliteHebdomadaireRepository;
import com.project.medisync.modules.disponibilites.service.DisponibiliteHebdomadaireService;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisponibiliteHebdomadaireServiceImpl implements DisponibiliteHebdomadaireService {

    private final DisponibiliteHebdomadaireRepository disponibiliteRepo;
    private final CreneauRepository                   creneauRepo;
    private final MedecinService                      medecinService; // interface publique Profils

    @Override
    @Transactional
    public DisponibiliteHebdomadaire create(UUID medecinId, JourSemaineEnum jour,
                                            LocalTime heureDebut, LocalTime heureFin) {

        // Vérifie que le médecin existe via l'interface publique du module Profils
        medecinService.getById(medecinId);

        validerPlage(heureDebut, heureFin);
        verifierChevauchement(medecinId, jour, heureDebut, heureFin, null);

        DisponibiliteHebdomadaire dispo = DisponibiliteHebdomadaire.builder()
                .medecinId(medecinId)
                .jourSemaine(jour)
                .heureDebut(heureDebut)
                .heureFin(heureFin)
                .build();

        DisponibiliteHebdomadaire saved = disponibiliteRepo.save(dispo);
        log.info("[Disponibilites] Plage créée — médecin {} {} {}→{}", medecinId, jour, heureDebut, heureFin);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibiliteHebdomadaire getById(UUID id) {
        return disponibiliteRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("DisponibiliteHebdomadaire", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibiliteHebdomadaire> getByMedecin(UUID medecinId) {
        return disponibiliteRepo.findByMedecinIdAndDeletedAtIsNull(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibiliteHebdomadaire> getActivesByMedecin(UUID medecinId) {
        return disponibiliteRepo.findByMedecinIdAndIsActiveTrueAndDeletedAtIsNull(medecinId);
    }

    @Override
    @Transactional
    public DisponibiliteHebdomadaire update(UUID id, JourSemaineEnum jour,
                                            LocalTime heureDebut, LocalTime heureFin) {
        DisponibiliteHebdomadaire dispo = getById(id);

        JourSemaineEnum  nouveauJour  = jour       != null ? jour       : dispo.getJourSemaine();
        LocalTime        nouveauDebut = heureDebut != null ? heureDebut : dispo.getHeureDebut();
        LocalTime        nouveauFin   = heureFin   != null ? heureFin   : dispo.getHeureFin();

        validerPlage(nouveauDebut, nouveauFin);
        verifierChevauchement(dispo.getMedecinId(), nouveauJour, nouveauDebut, nouveauFin, id);

        dispo.setJourSemaine(nouveauJour);
        dispo.setHeureDebut(nouveauDebut);
        dispo.setHeureFin(nouveauFin);

        log.info("[Disponibilites] Plage {} mise à jour.", id);
        return disponibiliteRepo.save(dispo);
    }

    @Override
    @Transactional
    public void toggleActive(UUID id, boolean isActive) {
        DisponibiliteHebdomadaire dispo = getById(id);
        dispo.setIsActive(isActive);
        disponibiliteRepo.save(dispo);

        // Si désactivation → soft-delete des créneaux futurs DISPONIBLES issus de cette règle
        if (!isActive) {
            creneauRepo.softDeleteFutursDisponibles(id, LocalDate.now());
            log.info("[Disponibilites] Plage {} désactivée — créneaux futurs supprimés.", id);
        } else {
            log.info("[Disponibilites] Plage {} réactivée.", id);
        }
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        DisponibiliteHebdomadaire dispo = getById(id);
        // Soft-delete des créneaux futurs DISPONIBLES issus de cette règle
        creneauRepo.softDeleteFutursDisponibles(id, LocalDate.now());
        dispo.setDeletedAt(LocalDateTime.now());
        disponibiliteRepo.save(dispo);
        log.info("[Disponibilites] Plage {} soft-deleted — créneaux futurs supprimés.", id);
    }

    // ── Méthodes privées ────────────────────────────────────────────────────

    private void validerPlage(LocalTime heureDebut, LocalTime heureFin) {
        if (!heureFin.isAfter(heureDebut)) {
            throw new BusinessException(
                    "L'heure de fin doit être postérieure à l'heure de début.");
        }
    }

    private void verifierChevauchement(UUID medecinId, JourSemaineEnum jour,
                                        LocalTime heureDebut, LocalTime heureFin,
                                        UUID excludeId) {
        if (disponibiliteRepo.existsChevauchement(medecinId, jour, heureDebut, heureFin, excludeId)) {

            // Récupère la plage conflictuelle pour afficher un message précis
            List<DisponibiliteHebdomadaire> existantes =
                    disponibiliteRepo.findByMedecinIdAndJourSemaineAndDeletedAtIsNull(medecinId, jour);

            String conflit = existantes.stream()
                    .filter(d -> excludeId == null || !d.getId().equals(excludeId))
                    .filter(d -> d.getHeureDebut().isBefore(heureFin) && heureDebut.isBefore(d.getHeureFin()))
                    .map(d -> d.getJourSemaine() + " " + d.getHeureDebut() + " → " + d.getHeureFin())
                    .findFirst()
                    .orElse("plage existante");

            throw new BusinessException(
                    "Cette plage chevauche une disponibilité existante — " + conflit + ".");
        }
    }
}
