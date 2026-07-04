package com.project.medisync.modules.reservations.service.impl;

import com.project.medisync.modules.disponibilites.service.CreneauService;
import com.project.medisync.modules.profils.service.DelegueService;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.modules.reservations.entity.*;
import com.project.medisync.modules.reservations.repository.PropositionRemplacementRepository;
import com.project.medisync.modules.reservations.repository.RendezVousRepository;
import com.project.medisync.modules.reservations.service.RendezVousService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository             rendezVousRepository;
    private final PropositionRemplacementRepository propositionRepository;
    private final CreneauService                   creneauService;
    private final DelegueService                   delegueService;
    private final MedecinService                   medecinService;

    @Override
    @Transactional
    public RendezVous reserver(UUID creneauId, UUID delegueId, UUID medecinId) {
        // 1. Créneau déjà réservé ?
        if (rendezVousRepository.existsByCreneauIdAndStatutNotAndDeletedAtIsNull(creneauId, StatutRendezVousEnum.ANNULE)) {
            throw new BusinessException("Ce créneau est déjà réservé.");
        }
        // 2. Délégué déjà occupé à cette heure ?
        if (rendezVousRepository.existsByDelegueConflict(delegueId, creneauId)) {
            throw new BusinessException("Vous avez déjà un rendez-vous à ce même créneau horaire.");
        }

        RendezVous rdv = RendezVous.builder()
                .creneau(creneauService.getById(creneauId))
                .delegue(delegueService.getById(delegueId))
                .medecin(medecinService.getById(medecinId))
                .statut(StatutRendezVousEnum.CONFIRME)
                .build();

        creneauService.marquerReserve(creneauId);
        return rendezVousRepository.save(rdv);
    }

    @Override
    @Transactional(readOnly = true)
    public RendezVous getById(UUID id) {
        return rendezVousRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getByDelegue(UUID delegueId) {
        return rendezVousRepository.findByDelegueIdAndDeletedAtIsNull(delegueId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> getByMedecin(UUID medecinId) {
        return rendezVousRepository.findByMedecinIdAndDeletedAtIsNull(medecinId);
    }

    @Override
    @Transactional
    public RendezVous annulerParDelegue(UUID rendezVousId) {
        RendezVous rdv = getById(rendezVousId);
        rdv.setStatut(StatutRendezVousEnum.ANNULE);
        rdv.setAnnulePar(AnnuleParEnum.DELEGUE);
        creneauService.marquerDisponible(rdv.getCreneau().getId());
        return rendezVousRepository.save(rdv);
    }

    @Override
    @Transactional
    public RendezVous annulerParMedecin(UUID rendezVousId, String motifAnnulation) {
        if (motifAnnulation == null || motifAnnulation.isBlank()) {
            throw new BusinessException("Le motif d'annulation est obligatoire lorsque c'est le médecin qui annule.");
        }
        RendezVous rdv = getById(rendezVousId);
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
    public RendezVous marquerRealise(UUID rendezVousId) {
        RendezVous rdv = getById(rendezVousId);
        rdv.setStatut(StatutRendezVousEnum.REALISE);
        return rendezVousRepository.save(rdv);
    }

    @Override
    @Transactional
    public RendezVous marquerAbsent(UUID rendezVousId) {
        RendezVous rdv = getById(rendezVousId);
        rdv.setStatut(StatutRendezVousEnum.ABSENT);
        return rendezVousRepository.save(rdv);
    }
}
