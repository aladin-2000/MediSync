package com.project.medisync.modules.disponibilites.service.impl;

import com.project.medisync.modules.disponibilites.entity.Creneau;
import com.project.medisync.modules.disponibilites.entity.StatutCreneauEnum;
import com.project.medisync.modules.disponibilites.repository.CreneauRepository;
import com.project.medisync.modules.disponibilites.service.CreneauService;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreneauServiceImpl implements CreneauService {

    private static final int DUREE_CRENEAU_MINUTES = 15;

    private final CreneauRepository                   creneauRepo;
    private final MedecinService                      medecinService;

    @Override
    @Transactional
    public Creneau createCreneau(String medecinId, LocalDate date, LocalTime heureDebut) {

        // Pas de doublon
        if (creneauRepo.existsByMedecinIdAndDateAndHeureDebut(medecinId, date, heureDebut)) {
            throw new BusinessException(
                    "Un créneau existe déjà le " + date + " à " + heureDebut + ".");
        }

        Creneau creneau = Creneau.builder()
                .medecinId(medecinId)
                .date(date)
                .heureDebut(heureDebut)
                .build();

        Creneau saved = creneauRepo.save(creneau);
        log.info("[Disponibilites] Créneau manuel créé — médecin {} le {} à {}", medecinId, date, heureDebut);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Creneau getById(String id) {
        return creneauRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Creneau> getBySemaine(String medecinId, LocalDate lundiDeLaSemaine) {
        LocalDate dimanche = lundiDeLaSemaine.plusDays(6);
        return creneauRepo.findByMedecinIdAndDateBetween(
                medecinId, lundiDeLaSemaine, dimanche);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Creneau> getDisponiblesPourRemplacement(String medecinId) {
        LocalDate debut = LocalDate.now();
        LocalDate fin   = LocalDate.now().plusWeeks(1);
        return creneauRepo.findByMedecinIdAndStatutAndDateBetween(
                medecinId, StatutCreneauEnum.DISPONIBLE, debut, fin);
    }

    @Override
    @Transactional
    public void marquerReserve(String id) {
        Creneau creneau = getById(id);
        creneau.setStatut(StatutCreneauEnum.RESERVE);
        creneauRepo.save(creneau);
        log.info("[Disponibilites] Créneau {} marqué comme RÉSERVÉ.", id);
    }

    @Override
    @Transactional
    public void marquerDisponible(String id) {
        Creneau creneau = getById(id);
        creneau.setStatut(StatutCreneauEnum.DISPONIBLE);
        creneauRepo.save(creneau);
        log.info("[Disponibilites] Créneau {} marqué comme DISPONIBLE.", id);
    }


}
