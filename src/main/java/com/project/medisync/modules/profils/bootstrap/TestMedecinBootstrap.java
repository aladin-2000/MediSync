package com.project.medisync.modules.profils.bootstrap;

import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crée un compte Médecin de test au démarrage de l'application, s'il n'existe pas encore.
 * Permet de tester l'app immédiatement sans passer par les 2 appels API (création du user + du profil médecin).
 *
 * Ne fait rien si TEST_MEDECIN_PASSWORD n'est pas défini (comportement par défaut,
 * sûr en production tant qu'on ne configure pas explicitement cette variable).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestMedecinBootstrap implements ApplicationRunner {

    private final UserService    userService;
    private final MedecinService medecinService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${test.medecin.email:medecin.test@medisync.tn}")
    private String medecinEmail;

    @Value("${test.medecin.password:}")
    private String medecinPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (medecinPassword == null || medecinPassword.isBlank()) {
            return;
        }

        User user;
        if (userService.existsByEmail(medecinEmail)) {
            user = userService.getByEmail(medecinEmail);
        } else {
            user = userService.save(User.builder()
                    .email(medecinEmail)
                    .passwordHash(passwordEncoder.encode(medecinPassword))
                    .role(RoleEnum.MEDECIN)
                    .mustChangePassword(false)
                    .build());
            log.info("[Profils] Utilisateur médecin de test créé : {}", medecinEmail);
        }

        try {
            medecinService.getByUserId(user.getId());
        } catch (ResourceNotFoundException e) {
            medecinService.create(user.getId(), "Test", "Médecin", "Médecine générale",
                    "1 rue de Test, Tunis", null, 36.8065, 10.1815, 0f);
            log.info("[Profils] Profil médecin de test créé pour {}", medecinEmail);
        }
    }
}
