package com.project.medisync.modules.profils.service.impl;

import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.service.EmailVerificationService;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.entity.Medecin;
import com.project.medisync.modules.profils.entity.SpecialiteEnum;
import com.project.medisync.modules.profils.repository.MedecinRepository;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.BusinessException;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final UserService        userService; // interface publique Auth — jamais UserRepository
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Medecin create(String userId, String nom, String prenom, SpecialiteEnum specialite,
                          String adresseCabinet, String telephone, Double latitude, Double longitude, Float scoreFiabiliteMin) {

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
                .telephone(telephone)
                .latitude(latitude)
                .longitude(longitude)
                .scoreFiabiliteMin(scoreFiabiliteMin != null ? scoreFiabiliteMin : 0f)
                .build();

        return medecinRepository.save(medecin);
    }

    @Override
    @Transactional
    public Medecin creerMedecinComplet(String email, String password, String nom, String prenom, SpecialiteEnum specialite,
                                        String adresseCabinet, String telephone, Double latitude, Double longitude, Float scoreFiabiliteMin) {

        if (userService.existsByEmail(email)) {
            throw new BusinessException("Un compte existe déjà avec l'adresse email : " + email);
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(RoleEnum.MEDECIN)
                .emailVerified(true)
                .build();
        User savedUser = userService.save(user);

        Medecin medecin = Medecin.builder()
                .user(savedUser)
                .nom(nom)
                .prenom(prenom)
                .specialite(specialite)
                .adresseCabinet(adresseCabinet)
                .telephone(telephone)
                .latitude(latitude)
                .longitude(longitude)
                .scoreFiabiliteMin(scoreFiabiliteMin != null ? scoreFiabiliteMin : 0f)
                .build();

        Medecin saved = medecinRepository.save(medecin);
        log.info("[Profils] Médecin complet créé — compte {} + profil {}.", savedUser.getId(), saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Medecin inscrire(String email, String password, String nom, String prenom, SpecialiteEnum specialite,
                             String adresseCabinet, String telephone, Double latitude, Double longitude) {

        if (userService.existsByEmail(email)) {
            throw new BusinessException("Un compte existe déjà avec l'adresse email : " + email);
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(RoleEnum.MEDECIN)
                .mustChangePassword(false)
                .emailVerified(false)
                .build();
        User savedUser = userService.save(user);

        Medecin medecin = Medecin.builder()
                .user(savedUser)
                .nom(nom)
                .prenom(prenom)
                .specialite(specialite)
                .adresseCabinet(adresseCabinet)
                .telephone(telephone)
                .latitude(latitude)
                .longitude(longitude)
                .valide(false)
                .build();

        Medecin saved = medecinRepository.save(medecin);
        emailVerificationService.genererEtEnvoyer(savedUser);
        log.info("[Profils] Auto-inscription médecin {} — en attente de vérification email + validation admin.", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getEnAttente() {
        return medecinRepository.findByValideFalse();
    }

    @Override
    @Transactional
    public Medecin valider(String id) {
        Medecin medecin = getById(id);
        medecin.setValide(true);
        Medecin saved = medecinRepository.save(medecin);
        log.info("[Profils] Médecin {} validé par un admin.", id);
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
    public List<Medecin> getByIds(List<String> ids) {
        return medecinRepository.findAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> searchByIdsNomSpecialites(List<String> ids, String nom, List<SpecialiteEnum> specialites) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<SpecialiteEnum> filtre = (specialites == null || specialites.isEmpty()) ? null : specialites;
        return medecinRepository.searchByIdsNomSpecialites(ids, nom, filtre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medecin> getBySpecialite(SpecialiteEnum specialite) {
        return medecinRepository.findBySpecialiteAndValideTrue(specialite);
    }

    @Override
    @Transactional
    public Medecin update(String id, String nom, String prenom, SpecialiteEnum specialite,
                          String adresseCabinet, String telephone, Double latitude, Double longitude, Float scoreFiabiliteMin) {

        Medecin medecin = getById(id);
        if (nom            != null) medecin.setNom(nom);
        if (prenom         != null) medecin.setPrenom(prenom);
        if (specialite     != null) medecin.setSpecialite(specialite);
        if (adresseCabinet != null) medecin.setAdresseCabinet(adresseCabinet);
        if (telephone      != null) medecin.setTelephone(telephone);
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
