package com.project.medisync.modules.profils.service.impl;

import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.entity.Laboratoire;
import com.project.medisync.modules.profils.entity.StatutAbonnementEnum;
import com.project.medisync.modules.profils.repository.LaboratoireRepository;
import com.project.medisync.modules.profils.service.LaboratoireService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LaboratoireServiceImpl implements LaboratoireService {

    private final LaboratoireRepository laboratoireRepository;
    private final UserService           userService;

    @Override
    @Transactional
    public Laboratoire create(String userId, String nom, String adresse,
                              StatutAbonnementEnum statut, LocalDate dateDebut, LocalDate dateFin) {

        if (!userService.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur", userId);
        }
        if (laboratoireRepository.existsByUserId(userId)) {
            throw new BusinessException("Un laboratoire est déjà associé à cet utilisateur.");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new BusinessException("La date de fin d'abonnement doit être postérieure à la date de début.");
        }

        Laboratoire labo = Laboratoire.builder()
                .user(userService.getById(userId))
                .nom(nom)
                .adresse(adresse)
                .statutAbonnement(statut)
                .dateDebutAbonnement(dateDebut)
                .dateFinAbonnement(dateFin)
                .build();

        return laboratoireRepository.save(labo);
    }

    @Override
    @Transactional(readOnly = true)
    public Laboratoire getById(String id) {
        return laboratoireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratoire", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Laboratoire> getAll() {
        return laboratoireRepository.findAll();
    }

    @Override
    @Transactional
    public Laboratoire update(String id, String nom, String adresse,
                              StatutAbonnementEnum statut, LocalDate dateDebut, LocalDate dateFin) {

        Laboratoire labo = getById(id);
        if (nom      != null) labo.setNom(nom);
        if (adresse  != null) labo.setAdresse(adresse);
        if (statut   != null) labo.setStatutAbonnement(statut);
        if (dateDebut != null) labo.setDateDebutAbonnement(dateDebut);
        if (dateFin   != null) {
            if (dateFin.isBefore(labo.getDateDebutAbonnement())) {
                throw new BusinessException("La date de fin doit être postérieure à la date de début.");
            }
            labo.setDateFinAbonnement(dateFin);
        }
        return laboratoireRepository.save(labo);
    }

    @Override
    @Transactional
    public void updateDernierPaiement(String laboratoireId, String paiementId) {
        Laboratoire labo = getById(laboratoireId);
        labo.setDernierPaiementId(paiementId);
        laboratoireRepository.save(labo);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!laboratoireRepository.existsById(id)) {
            throw new ResourceNotFoundException("Laboratoire", id);
        }
        laboratoireRepository.deleteById(id);
        log.info("[Profils] Laboratoire {} supprimé.", id);
    }
}
