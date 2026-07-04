package com.project.medisync.modules.acces.service.impl;

import com.project.medisync.modules.acces.entity.BlocagePriorite;
import com.project.medisync.modules.acces.entity.TypeBlocageEnum;
import com.project.medisync.modules.acces.repository.BlocagePrioriteRepository;
import com.project.medisync.modules.acces.service.BlocagePrioriteService;
import com.project.medisync.modules.profils.service.DelegueService;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlocagePrioriteServiceImpl implements BlocagePrioriteService {

    private final BlocagePrioriteRepository blocageRepository;
    private final MedecinService            medecinService;
    private final DelegueService            delegueService;

    @Override
    @Transactional
    public BlocagePriorite definir(UUID medecinId, UUID delegueId, TypeBlocageEnum type) {
        // Upsert : si une règle existe déjà, on la met à jour
        return blocageRepository.findByMedecinIdAndDelegueId(medecinId, delegueId)
                .map(existant -> {
                    existant.setType(type);
                    return blocageRepository.save(existant);
                })
                .orElseGet(() -> blocageRepository.save(
                        BlocagePriorite.builder()
                                .medecin(medecinService.getById(medecinId))
                                .delegue(delegueService.getById(delegueId))
                                .type(type)
                                .build()));
    }

    @Override
    @Transactional(readOnly = true)
    public BlocagePriorite getByMedecinAndDelegue(UUID medecinId, UUID delegueId) {
        return blocageRepository.findByMedecinIdAndDelegueId(medecinId, delegueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucune règle d'accès définie entre ce médecin et ce délégué."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlocagePriorite> getByMedecin(UUID medecinId, TypeBlocageEnum type) {
        return blocageRepository.findByMedecinIdAndType(medecinId, type);
    }

    @Override
    @Transactional
    public void supprimer(UUID medecinId, UUID delegueId) {
        BlocagePriorite blocage = getByMedecinAndDelegue(medecinId, delegueId);
        blocageRepository.delete(blocage);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estBloque(UUID medecinId, UUID delegueId) {
        return blocageRepository.existsByMedecinIdAndDelegueIdAndType(medecinId, delegueId, TypeBlocageEnum.BLOQUE);
    }
}
