package com.project.medisync.modules.analytics.service.impl;

import com.project.medisync.modules.analytics.entity.MedecinPrioritaire;
import com.project.medisync.modules.analytics.repository.MedecinPrioritaireRepository;
import com.project.medisync.modules.analytics.service.MedecinPrioritaireService;
import com.project.medisync.modules.profils.service.LaboratoireService;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedecinPrioritaireServiceImpl implements MedecinPrioritaireService {

    private final MedecinPrioritaireRepository repository;
    private final LaboratoireService            laboratoireService;
    private final MedecinService                medecinService;

    @Override
    @Transactional
    public MedecinPrioritaire ajouter(String laboratoireId, String medecinId) {
        if (repository.existsByLaboratoireIdAndMedecinId(laboratoireId, medecinId)) {
            throw new BusinessException("Ce médecin est déjà dans la liste prioritaire de ce laboratoire.");
        }
        return repository.save(MedecinPrioritaire.builder()
                .laboratoire(laboratoireService.getById(laboratoireId))
                .medecin(medecinService.getById(medecinId))
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedecinPrioritaire> getByLaboratoire(String laboratoireId) {
        return repository.findByLaboratoireId(laboratoireId);
    }

    @Override
    @Transactional
    public void retirer(String laboratoireId, String medecinId) {
        MedecinPrioritaire entry = repository.findByLaboratoireIdAndMedecinId(laboratoireId, medecinId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ce médecin n'est pas dans la liste prioritaire de ce laboratoire."));
        repository.delete(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estPrioritaire(String laboratoireId, String medecinId) {
        return repository.existsByLaboratoireIdAndMedecinId(laboratoireId, medecinId);
    }
}
