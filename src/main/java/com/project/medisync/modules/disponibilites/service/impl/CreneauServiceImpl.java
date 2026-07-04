package com.project.medisync.modules.disponibilites.service.impl;

import com.project.medisync.modules.disponibilites.entity.Creneau;
import com.project.medisync.modules.disponibilites.entity.DisponibiliteHebdomadaire;
import com.project.medisync.modules.disponibilites.entity.StatutCreneauEnum;
import com.project.medisync.modules.disponibilites.repository.CreneauRepository;
import com.project.medisync.modules.disponibilites.repository.DisponibiliteHebdomadaireRepository;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreneauServiceImpl implements CreneauService {

    private static final int DUREE_CRENEAU_MINUTES = 15;

    private final CreneauRepository                   creneauRepo;
    private final DisponibiliteHebdomadaireRepository disponibiliteRepo;
    private final MedecinService                      medecinService;

    @Override
    @Transactional
    public Creneau createManuel(UUID medecinId, LocalDate date, LocalTime heureDebut) {

        medecinService.getById(medecinId);

        // Max 1 semaine à l'avance
        LocalDate limiteMax = LocalDate.now().plusWeeks(1);
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessException("Impossible de créer un créneau dans le passé.");
        }
        if (date.isAfter(limiteMax)) {
            throw new BusinessException(
                    "La réservation est limitée à une semaine à l'avance. Date maximale : " + limiteMax + ".");
        }

        // Pas de doublon
        if (creneauRepo.existsByMedecinIdAndDateAndHeureDebutAndDeletedAtIsNull(medecinId, date, heureDebut)) {
            throw new BusinessException(
                    "Un créneau existe déjà le " + date + " à " + heureDebut + ".");
        }

        Creneau creneau = Creneau.builder()
                .medecinId(medecinId)
                .date(date)
                .heureDebut(heureDebut)
                // disponibiliteHebdoId = null → créneau manuel
                .build();

        Creneau saved = creneauRepo.save(creneau);
        log.info("[Disponibilites] Créneau manuel créé — médecin {} le {} à {}", medecinId, date, heureDebut);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Creneau getById(UUID id) {
        return creneauRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Creneau> getBySemaine(UUID medecinId, LocalDate lundiDeLaSemaine) {
        LocalDate dimanche = lundiDeLaSemaine.plusDays(6);
        return creneauRepo.findByMedecinIdAndDateBetweenAndDeletedAtIsNull(
                medecinId, lundiDeLaSemaine, dimanche);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Creneau> getDisponiblesPourRemplacement(UUID medecinId) {
        LocalDate debut = LocalDate.now();
        LocalDate fin   = LocalDate.now().plusWeeks(1);
        return creneauRepo.findByMedecinIdAndStatutAndDateBetweenAndDeletedAtIsNull(
                medecinId, StatutCreneauEnum.DISPONIBLE, debut, fin);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Creneau creneau = getById(id);

        if (creneau.getStatut() == StatutCreneauEnum.RESERVE) {
            throw new BusinessException(
                    "Impossible de supprimer un créneau déjà réservé. " +
                    "Veuillez passer par l'annulation de rendez-vous.");
        }

        creneau.setDeletedAt(LocalDateTime.now());
        creneauRepo.save(creneau);
        log.info("[Disponibilites] Créneau {} soft-deleted.", id);
    }

    /**
     * Job automatique — s'exécute chaque vendredi à 22h00.
     * Lit toutes les DisponibilitéHebdomadaires actives et génère
     * les créneaux de 15 minutes pour la semaine suivante.
     */
    @Override
    @Scheduled(cron = "0 0 22 * * FRI")
    @Transactional
    public void genererCreneauxSemaineProchaine() {
        log.info("[Disponibilites] Job démarré — génération des créneaux pour la semaine prochaine.");

        // Calcule le lundi de la semaine prochaine
        LocalDate lundiProchain = LocalDate.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        List<DisponibiliteHebdomadaire> regles =
                disponibiliteRepo.findByIsActiveTrueAndDeletedAtIsNull();

        int total = 0;
        List<Creneau> aInserer = new ArrayList<>();

        for (DisponibiliteHebdomadaire regle : regles) {

            // Calcule la date concrète du jour de la semaine prochaine
            LocalDate dateJour = lundiProchain.with(
                    TemporalAdjusters.nextOrSame(toDayOfWeek(regle.getJourSemaine())));

            // Découpe la plage en slots de 15 minutes
            LocalTime cursor = regle.getHeureDebut();
            while (cursor.plusMinutes(DUREE_CRENEAU_MINUTES).compareTo(regle.getHeureFin()) <= 0) {

                // Anti-doublon — ne recrée pas un créneau déjà existant
                if (!creneauRepo.existsByMedecinIdAndDateAndHeureDebutAndDeletedAtIsNull(
                        regle.getMedecinId(), dateJour, cursor)) {

                    aInserer.add(Creneau.builder()
                            .medecinId(regle.getMedecinId())
                            .disponibiliteHebdoId(regle.getId())
                            .date(dateJour)
                            .heureDebut(cursor)
                            .build());
                    total++;
                }
                cursor = cursor.plusMinutes(DUREE_CRENEAU_MINUTES);
            }
        }

        creneauRepo.saveAll(aInserer);
        log.info("[Disponibilites] Job terminé — {} créneaux générés pour la semaine du {}.",
                total, lundiProchain);
    }

    @Override
    @Transactional
    public void marquerReserve(UUID id) {
        Creneau creneau = getById(id);
        creneau.setStatut(StatutCreneauEnum.RESERVE);
        creneauRepo.save(creneau);
        log.info("[Disponibilites] Créneau {} marqué comme RÉSERVÉ.", id);
    }

    @Override
    @Transactional
    public void marquerDisponible(UUID id) {
        Creneau creneau = getById(id);
        creneau.setStatut(StatutCreneauEnum.DISPONIBLE);
        creneauRepo.save(creneau);
        log.info("[Disponibilites] Créneau {} marqué comme DISPONIBLE.", id);
    }

    // ── Utilitaires ─────────────────────────────────────────────────────────

    private DayOfWeek toDayOfWeek(com.project.medisync.modules.disponibilites.entity.JourSemaineEnum jour) {
        return switch (jour) {
            case LUNDI    -> DayOfWeek.MONDAY;
            case MARDI    -> DayOfWeek.TUESDAY;
            case MERCREDI -> DayOfWeek.WEDNESDAY;
            case JEUDI    -> DayOfWeek.THURSDAY;
            case VENDREDI -> DayOfWeek.FRIDAY;
            case SAMEDI   -> DayOfWeek.SATURDAY;
        };
    }
}
