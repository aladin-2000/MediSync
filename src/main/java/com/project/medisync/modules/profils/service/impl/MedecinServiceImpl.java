package com.project.medisync.modules.profils.service.impl;

import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.entity.Medecin;
import com.project.medisync.modules.profils.repository.MedecinRepository;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public Medecin create(String userId, String nom, String prenom, String specialite,
                          String adresseCabinet, Double latitude, Double longitude, Float scoreFiabiliteMin) {

        if (!userService.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur", userId);
        }
        if (medecinRepository.existsByUserId(userId)) {
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
    @Transactional
    public Medecin creerMedecinComplet(String email, String password, String nom, String prenom, String specialite,
                                        String adresseCabinet, Double latitude, Double longitude, Float scoreFiabiliteMin) {

        if (userService.existsByEmail(email)) {
            throw new BusinessException("Un compte existe déjà avec l'adresse email : " + email);
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(RoleEnum.MEDECIN)
                .build();
        User savedUser = userService.save(user);

        Medecin medecin = Medecin.builder()
                .user(savedUser)
                .nom(nom)
                .prenom(prenom)
                .specialite(specialite)
                .adresseCabinet(adresseCabinet)
                .latitude(latitude)
                .longitude(longitude)
                .scoreFiabiliteMin(scoreFiabiliteMin != null ? scoreFiabiliteMin : 0f)
                .build();

        Medecin saved = medecinRepository.save(medecin);
        log.info("[Profils] Médecin complet créé — compte {} + profil {}.", savedUser.getId(), saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Medecin getById(String id) {
        return medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Medecin getByUserId(String userId) {
        return medecinRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin pour l'utilisateur", userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getAll() {
        return medecinRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getBySpecialite(String specialite) {
        return medecinRepository.findBySpecialiteContainingIgnoreCase(specialite);
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
        medecinRepository.save(medecin);
        log.info("[Profils] Médecin {} soft-deleted.", id);
    }
}
