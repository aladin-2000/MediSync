package com.project.medisync.modules.profils.service.impl;

import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.entity.Medecin;
import com.project.medisync.modules.profils.repository.MedecinRepository;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final UserService        userService; // interface publique Auth — jamais UserRepository

    @Override
    @Transactional
    public Medecin create(String userId, String nom, String prenom, String specialite,
                          String adresseCabinet, Double latitude, Double longitude, Float scoreFiabiliteMin) {

        if (!userService.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur", userId);
        }
        if (medecinRepository.existsByUserIdAndDeletedAtIsNull(userId)) {
            throw new BusinessException("Un profil médecin existe déjà pour cet utilisateur.");
        }

        Medecin medecin = Medecin.builder()
                .user(userService.getById(userId))
                .nom(nom)
                .prenom(prenom)
                .specialite(specialite)
                .adresseCabinet(adresseCabinet)
                .latitude(latitude)
                .longitude(longitude)
                .scoreFiabiliteMin(scoreFiabiliteMin != null ? scoreFiabiliteMin : 0f)
                .build();

        return medecinRepository.save(medecin);
    }

    @Override
    @Transactional(readOnly = true)
    public Medecin getById(String id) {
        return medecinRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getAll() {
        return medecinRepository.findAllByDeletedAtIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getBySpecialite(String specialite) {
        return medecinRepository.findBySpecialiteContainingIgnoreCaseAndDeletedAtIsNull(specialite);
    }

    @Override
    @Transactional
    public Medecin update(String id, String nom, String prenom, String specialite,
                          String adresseCabinet, Double latitude, Double longitude, Float scoreFiabiliteMin) {

        Medecin medecin = getById(id);
        if (nom            != null) medecin.setNom(nom);
        if (prenom         != null) medecin.setPrenom(prenom);
        if (specialite     != null) medecin.setSpecialite(specialite);
        if (adresseCabinet != null) medecin.setAdresseCabinet(adresseCabinet);
        if (latitude       != null) medecin.setLatitude(latitude);
        if (longitude      != null) medecin.setLongitude(longitude);
        if (scoreFiabiliteMin != null) medecin.setScoreFiabiliteMin(scoreFiabiliteMin);
        return medecinRepository.save(medecin);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Medecin medecin = getById(id);
        medecin.setDeletedAt(LocalDateTime.now());
        medecinRepository.save(medecin);
        log.info("[Profils] Médecin {} soft-deleted.", id);
    }
}
