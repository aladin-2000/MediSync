package com.project.medisync.modules.reservations.service.impl;

import com.project.medisync.modules.disponibilites.service.CreneauService;
import com.project.medisync.modules.profils.service.DelegueService;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.modules.reservations.entity.*;
import com.project.medisync.modules.reservations.repository.PropositionRemplacementRepository;
import com.project.medisync.modules.reservations.repository.RendezVousRepository;
import com.project.medisync.modules.reservations.repository.VisiteRepository;
import com.project.medisync.modules.reservations.service.RendezVousService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.String;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository             rendezVousRepository;
    private final PropositionRemplacementRepository propositionRepository;
    private final VisiteRepository                 visiteRepository;
    private final CreneauService                   creneauService;
    private final DelegueService                   delegueService;
    private final MedecinService                   medecinService;

    @Override
    @Transactional
    public RendezVous reserver(String creneauId, String delegueId, String medecinId) {
        // 1. Créneau déjà réservé ?
        if (rendezVousRepository.existsByCreneauIdAndStatutNot(creneauId, StatutRendezVousEnum.ANNULE)) {
            throw new BusinessException("Ce créneau est déjà réservé.");
        }
        // 2. Délégué déjà occupé à cette heure ?
        if (rendezVousRepository.existsByDelegueConflict(delegueId, creneauId)) {
            throw new BusinessException("Vous avez déjà un rendez-vous à ce même créneau horaire.");
        }

        var delegue = delegueService.getById(delegueId);

        RendezVous rdv = RendezVous.builder()
                .creneau(creneauService.getById(creneauId))
                .delegue(delegue)
                .medecin(medecinService.getById(medecinId))
                .laboratoire(delegue.getLaboratoire())
                .statut(StatutRendezVousEnum.CONFIRME)
                .build();

        creneauService.marquerReserve(creneauId);
        return rendezVousRepository.save(rdv);
    }

    @Override
    @Transactional(readOnly = true)
    public RendezVous getById(String id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getByDelegue(String delegueId) {
        return rendezVousRepository.findByDelegueId(delegueId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getByDelegueEtJour(String delegueId, LocalDate date) {
        return rendezVousRepository.findByDelegueIdAndCreneau_Date(delegueId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getByDelegueEtSemaine(String delegueId, LocalDate lundiDeLaSemaine) {
        LocalDate dimanche = lundiDeLaSemaine.plusDays(6);
        return rendezVousRepository.findByDelegueIdAndCreneau_DateBetween(
                delegueId, lundiDeLaSemaine, dimanche);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getByMedecin(String medecinId) {
        return rendezVousRepository.findByMedecinId(medecinId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getByMedecinEtJour(String medecinId, LocalDate date) {
        return rendezVousRepository.findByMedecinIdAndCreneau_Date(medecinId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getByMedecinEtSemaine(String medecinId, LocalDate lundiDeLaSemaine) {
        LocalDate dimanche = lundiDeLaSemaine.plusDays(6);
        return rendezVousRepository.findByMedecinIdAndCreneau_DateBetween(
                medecinId, lundiDeLaSemaine, dimanche);
    }

    @Override
    @Transactional
    public RendezVous annulerParDelegue(String rendezVousId) {
        RendezVous rdv = getById(rendezVousId);
        verifierEncoreModifiable(rdv);
        rdv.setStatut(StatutRendezVousEnum.ANNULE);
        rdv.setAnnulePar(AnnuleParEnum.DELEGUE);
        creneauService.marquerDisponible(rdv.getCreneau().getId());
        return rendezVousRepository.save(rdv);
    }

    @Override
    @Transactional
    public RendezVous annulerParMedecin(String rendezVousId, String motifAnnulation) {
        if (motifAnnulation == null || motifAnnulation.isBlank()) {
            throw new BusinessException("Le motif d'annulation est obligatoire lorsque c'est le médecin qui annule.");
        }
        RendezVous rdv = getById(rendezVousId);
        verifierEncoreModifiable(rdv);
        rdv.setStatut(StatutRendezVousEnum.ANNULE);
        rdv.setAnnulePar(AnnuleParEnum.MEDECIN);
        rdv.setMotifAnnulation(motifAnnulation);
        creneauService.marquerDisponible(rdv.getCreneau().getId());
        rendezVousRepository.save(rdv);

        // Création automatique d'une PropositionRemplacement
        PropositionRemplacement proposition = PropositionRemplacement.builder()
                .rendezVousAnnule(rdv)
                .delegue(rdv.getDelegue())
                .medecin(rdv.getMedecin())
                .statut(StatutPropositionEnum.EN_ATTENTE)
                .build();
        propositionRepository.save(proposition);
        log.info("[Réservations] PropositionRemplacement créée pour le RDV annulé {}.", rendezVousId);

        return rdv;
    }

    @Override
    @Transactional
    public RendezVous realiserParDelegue(String rendezVousId) {
        RendezVous rdv = getById(rendezVousId);
        verifierEncoreModifiable(rdv);
        rdv.setRealiseParDelegue(true);
        return confirmerSiLesDeuxPartiesOntValide(rdv);
    }

    @Override
    @Transactional
    public RendezVous realiserParMedecin(String rendezVousId) {
        RendezVous rdv = getById(rendezVousId);
        verifierEncoreModifiable(rdv);
        rdv.setRealiseParMedecin(true);
        return confirmerSiLesDeuxPartiesOntValide(rdv);
    }

    /** Si les deux parties ont confirmé, passe le RDV à REALISE et crée la Visite (preuve de facturation). */
    private RendezVous confirmerSiLesDeuxPartiesOntValide(RendezVous rdv) {
        if (rdv.getRealiseParDelegue() && rdv.getRealiseParMedecin()) {
            rdv.setStatut(StatutRendezVousEnum.REALISE);
            rendezVousRepository.save(rdv);

            Visite visite = Visite.builder().rendezVous(rdv).build();
            visiteRepository.save(visite);
            log.info("[Réservations] RDV {} réalisé (double confirmation) — Visite créée.", rdv.getId());
            return rdv;
        }
        return rendezVousRepository.save(rdv);
    }

    @Override
    @Transactional
    public RendezVous marquerAbsent(String rendezVousId) {
        RendezVous rdv = getById(rendezVousId);
        verifierEncoreModifiable(rdv);
        rdv.setStatut(StatutRendezVousEnum.ABSENT);
        return rendezVousRepository.save(rdv);
    }

    /**
     * Une fois le RDV REALISE, ANNULE ou ABSENT, plus aucune action ne doit pouvoir le faire
     * changer d'état (protège la Visite/facturation d'une annulation a posteriori).
     */
    private void verifierEncoreModifiable(RendezVous rdv) {
        if (rdv.getStatut() != StatutRendezVousEnum.CONFIRME) {
            throw new BusinessException(
                    "Ce rendez-vous ne peut plus être modifié (statut actuel : " + rdv.getStatut() + ").");
        }
    }
}
