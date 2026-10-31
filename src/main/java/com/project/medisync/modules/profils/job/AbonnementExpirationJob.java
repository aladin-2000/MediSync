package com.project.medisync.modules.profils.job;

import com.project.medisync.modules.profils.entity.Laboratoire;
import com.project.medisync.modules.profils.entity.StatutAbonnementEnum;
import com.project.medisync.modules.profils.repository.LaboratoireRepository;
import com.project.medisync.modules.profils.service.LaboratoireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbonnementExpirationJob {

    private final LaboratoireRepository laboratoireRepository;
    private final LaboratoireService laboratoireService;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void verifierAbonnementsExpires() {
        List<Laboratoire> expires = laboratoireRepository
                .findByDateFinAbonnementBeforeAndStatutAbonnementNot(
                        LocalDate.now(), StatutAbonnementEnum.SUSPENDU);

        for (Laboratoire labo : expires) {
            laboratoireService.desactiver(labo.getId());
            log.info("[Job] Labo {} suspendu (abonnement expiré le {}).",
                    labo.getNom(), labo.getDateFinAbonnement());
        }

        if (!expires.isEmpty()) {
            log.info("[Job] {} laboratoire(s) suspendu(s) pour abonnement expiré.", expires.size());
        }
    }
}
