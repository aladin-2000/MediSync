package com.project.medisync.modules.reservations.job;

import com.project.medisync.modules.reservations.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Auto-valide les RDV bloqués avec une seule confirmation de réalisation
 * (silence de l'autre partie pendant 24h après l'heure du RDV = présomption que la visite a eu lieu).
 */
@Component
@RequiredArgsConstructor
public class ConfirmationExpireeJob {

    private final RendezVousService rendezVousService;

    @Scheduled(fixedRate = 60 * 60 * 1000) // toutes les heures
    public void executer() {
        rendezVousService.resoudreConfirmationsPartiellesExpirees();
    }
}
