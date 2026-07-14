package com.project.medisync.modules.reservations.service.impl;

import com.project.medisync.modules.disponibilites.service.CreneauService;
import com.project.medisync.modules.reservations.entity.PropositionRemplacement;
import com.project.medisync.modules.reservations.entity.RendezVous;
import com.project.medisync.modules.reservations.entity.StatutPropositionEnum;
import com.project.medisync.modules.reservations.entity.StatutRendezVousEnum;
import com.project.medisync.modules.reservations.repository.PropositionRemplacementRepository;
import com.project.medisync.modules.reservations.service.PropositionRemplacementService;
import com.project.medisync.modules.reservations.service.RendezVousService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropositionRemplacementServiceImpl implements PropositionRemplacementService {

    private final PropositionRemplacementRepository propositionRepository;
    private final RendezVousService                  rendezVousService;
    private final CreneauService                     creneauService;

    @Override
    @Transactional(readOnly = true)
    public PropositionRemplacement getById(String id) {
        return propositionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PropositionRemplacement", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropositionRemplacement> getEnAttenteByDelegue(String delegueId) {
        return propositionRepository.findByDelegueIdAndStatut(delegueId, StatutPropositionEnum.EN_ATTENTE);
    }

    @Override
    @Transactional
    public PropositionRemplacement accepter(String propositionId, String nouveauCreneauId) {
        PropositionRemplacement proposition = getById(propositionId);
        if (proposition.getStatut() != StatutPropositionEnum.EN_ATTENTE) {
            throw new BusinessException("Cette proposition n'est plus en attente.");
        }
        // Créer un nouveau rendez-vous sur le créneau choisi
        RendezVous nouveauRdv = rendezVousService.reserver(
                nouveauCreneauId,
                proposition.getDelegue().getId(),
                proposition.getMedecin().getId());

        proposition.setRendezVousNouveau(nouveauRdv);
        proposition.setStatut(StatutPropositionEnum.ACCEPTE);
        return propositionRepository.save(proposition);
    }

    /**
     * Job planifié : toutes les heures, expire les propositions restées
     * EN_ATTENTE depuis plus de 48 heures.
     */
    @Override
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expirePropositionsNonAcceptees() {
        List<PropositionRemplacement> enAttente =
                propositionRepository.findByStatut(StatutPropositionEnum.EN_ATTENTE);

        java.time.LocalDateTime limite = java.time.LocalDateTime.now().minusHours(48);
        int count = 0;
        for (PropositionRemplacement p : enAttente) {
            if (p.getCreatedAt().isBefore(limite)) {
                p.setStatut(StatutPropositionEnum.EXPIRE);
                propositionRepository.save(p);
                count++;
            }
        }
        if (count > 0) log.info("[Scheduler] {} propositions expirées.", count);
    }
}
