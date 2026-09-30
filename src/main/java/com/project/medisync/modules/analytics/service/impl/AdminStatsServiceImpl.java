package com.project.medisync.modules.analytics.service.impl;

import com.project.medisync.modules.analytics.dto.DashboardCountsResponse;
import com.project.medisync.modules.analytics.dto.StatsVisitesDelegueResponse;
import com.project.medisync.modules.analytics.dto.StatsVisitesLaboResponse;
import com.project.medisync.modules.analytics.service.AdminStatsService;
import com.project.medisync.modules.profils.repository.DelegueRepository;
import com.project.medisync.modules.profils.repository.LaboratoireRepository;
import com.project.medisync.modules.profils.repository.MedecinRepository;
import com.project.medisync.modules.reservations.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsServiceImpl implements AdminStatsService {

    private final RendezVousRepository rendezVousRepository;
    private final MedecinRepository medecinRepository;
    private final DelegueRepository delegueRepository;
    private final LaboratoireRepository laboratoireRepository;

    @Override
    public DashboardCountsResponse getDashboardCounts() {
        return new DashboardCountsResponse(
                medecinRepository.count(),
                delegueRepository.count(),
                laboratoireRepository.count()
        );
    }

    @Override
    public List<StatsVisitesLaboResponse> getVisitesParLaboratoire(Integer mois, Integer annee) {
        LocalDate[] bornes = resoudrePeriode(mois, annee);
        return rendezVousRepository.countVisitesParLaboratoire(bornes[0], bornes[1]).stream()
                .map(row -> new StatsVisitesLaboResponse(
                        (String) row[0],
                        (String) row[1],
                        (Long) row[2]
                ))
                .toList();
    }

    @Override
    public List<StatsVisitesDelegueResponse> getVisitesParDelegue(Integer mois, Integer annee) {
        LocalDate[] bornes = resoudrePeriode(mois, annee);
        return rendezVousRepository.countVisitesParDelegue(bornes[0], bornes[1]).stream()
                .map(row -> new StatsVisitesDelegueResponse(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (Long) row[5]
                ))
                .toList();
    }

    @Override
    public List<StatsVisitesDelegueResponse> getVisitesParDelegueForLaboratoire(String laboratoireId, Integer mois, Integer annee) {
        LocalDate[] bornes = resoudrePeriode(mois, annee);
        return rendezVousRepository.countVisitesParDelegueForLaboratoire(laboratoireId, bornes[0], bornes[1]).stream()
                .map(row -> new StatsVisitesDelegueResponse(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        null,
                        null,
                        (Long) row[3]
                ))
                .toList();
    }

    private LocalDate[] resoudrePeriode(Integer mois, Integer annee) {
        if (mois == null || annee == null) {
            return new LocalDate[]{null, null};
        }
        YearMonth ym = YearMonth.of(annee, mois);
        return new LocalDate[]{ym.atDay(1), ym.atEndOfMonth()};
    }
}
