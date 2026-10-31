package com.project.medisync.modules.reservations.service.impl;

import com.project.medisync.modules.profils.repository.DelegueRepository;
import com.project.medisync.modules.profils.repository.LaboratoireRepository;
import com.project.medisync.modules.profils.repository.MedecinRepository;
import com.project.medisync.modules.reservations.dto.DashboardCountsResponse;
import com.project.medisync.modules.reservations.dto.StatsVisitesDelegueResponse;
import com.project.medisync.modules.reservations.dto.StatsVisitesLaboResponse;
import com.project.medisync.modules.reservations.repository.RendezVousRepository;
import com.project.medisync.modules.reservations.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService {

    private final MedecinRepository medecinRepository;
    private final DelegueRepository delegueRepository;
    private final LaboratoireRepository laboratoireRepository;
    private final RendezVousRepository rendezVousRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardCountsResponse getDashboardCounts() {
        return DashboardCountsResponse.builder()
                .totalMedecins(medecinRepository.count())
                .totalDelegues(delegueRepository.count())
                .totalLaboratoires(laboratoireRepository.count())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsVisitesLaboResponse> getVisitesParLaboratoire(Integer mois, Integer annee) {
        LocalDate[] periode = toPeriode(mois, annee);
        return rendezVousRepository.countVisitesParLaboratoire(periode[0], periode[1])
                .stream()
                .map(row -> StatsVisitesLaboResponse.builder()
                        .laboratoireId((String) row[0])
                        .laboratoireNom((String) row[1])
                        .nombreVisites((Long) row[2])
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsVisitesDelegueResponse> getVisitesParDelegue(Integer mois, Integer annee) {
        LocalDate[] periode = toPeriode(mois, annee);
        return rendezVousRepository.countVisitesParDelegue(periode[0], periode[1])
                .stream()
                .map(row -> StatsVisitesDelegueResponse.builder()
                        .delegueId((String) row[0])
                        .delegueNom((String) row[1])
                        .deleguePrenom((String) row[2])
                        .laboratoireId((String) row[3])
                        .laboratoireNom((String) row[4])
                        .nombreVisites((Long) row[5])
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsVisitesDelegueResponse> getVisitesParDelegueForLabo(String laboratoireId, Integer mois, Integer annee) {
        LocalDate[] periode = toPeriode(mois, annee);
        return rendezVousRepository.countVisitesParDelegueForLaboratoire(laboratoireId, periode[0], periode[1])
                .stream()
                .map(row -> StatsVisitesDelegueResponse.builder()
                        .delegueId((String) row[0])
                        .delegueNom((String) row[1])
                        .deleguePrenom((String) row[2])
                        .nombreVisites((Long) row[3])
                        .build())
                .toList();
    }

    private LocalDate[] toPeriode(Integer mois, Integer annee) {
        if (mois == null || annee == null) {
            return new LocalDate[]{null, null};
        }
        LocalDate debut = LocalDate.of(annee, mois, 1);
        LocalDate fin = debut.withDayOfMonth(debut.lengthOfMonth());
        return new LocalDate[]{debut, fin};
    }
}
