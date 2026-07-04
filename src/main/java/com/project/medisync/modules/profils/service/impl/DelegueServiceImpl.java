package com.project.medisync.modules.profils.service.impl;

import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.entity.Delegue;
import com.project.medisync.modules.profils.repository.DelegueRepository;
import com.project.medisync.modules.profils.service.DelegueService;
import com.project.medisync.modules.profils.service.LaboratoireService;
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
public class DelegueServiceImpl implements DelegueService {

    private final DelegueRepository  delegueRepository;
    private final UserService        userService;
    private final LaboratoireService laboratoireService;

    @Override
    @Transactional
    public Delegue create(UUID userId, UUID laboratoireId, String nom, String prenom, String telephone) {

        if (!userService.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur", userId);
        }
        if (delegueRepository.existsByUserIdAndDeletedAtIsNull(userId)) {
            throw new BusinessException("Un profil délégué existe déjà pour cet utilisateur.");
        }

        Delegue delegue = Delegue.builder()
                .user(userService.getById(userId))
                .laboratoire(laboratoireService.getById(laboratoireId))
                .nom(nom)
                .prenom(prenom)
                .telephone(telephone)
                .build();

        return delegueRepository.save(delegue);
    }

    @Override
    @Transactional(readOnly = true)
    public Delegue getById(UUID id) {
        return delegueRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Délégué", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Delegue> getAll() {
        return delegueRepository.findAllByDeletedAtIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Delegue> getByLaboratoire(UUID laboratoireId) {
        return delegueRepository.findByLaboratoireIdAndDeletedAtIsNull(laboratoireId);
    }

    @Override
    @Transactional
    public Delegue update(UUID id, String nom, String prenom, String telephone) {
        Delegue delegue = getById(id);
        if (nom       != null) delegue.setNom(nom);
        if (prenom    != null) delegue.setPrenom(prenom);
        if (telephone != null) delegue.setTelephone(telephone);
        return delegueRepository.save(delegue);
    }

    @Override
    @Transactional
    public void updateScoreFiabilite(UUID id, Float nouveauScore) {
        if (nouveauScore < 0 || nouveauScore > 100) {
            throw new BusinessException("Le score de fiabilité doit être compris entre 0 et 100.");
        }
        Delegue delegue = getById(id);
        delegue.setScoreFiabilite(nouveauScore);
        delegueRepository.save(delegue);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Delegue delegue = getById(id);
        delegue.setDeletedAt(LocalDateTime.now());
        delegueRepository.save(delegue);
        log.info("[Profils] Délégué {} soft-deleted.", id);
    }
}
