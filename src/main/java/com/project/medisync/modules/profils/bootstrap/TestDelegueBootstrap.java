package com.project.medisync.modules.profils.bootstrap;

import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.service.DelegueService;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crée un compte Délégué de test au démarrage de l'application, s'il n'existe pas encore.
 * Permet de tester l'app immédiatement sans passer par les 2 appels API (création du user + du profil délégué).
 *
 * Ne fait rien si TEST_DELEGUE_PASSWORD n'est pas défini (comportement par défaut,
 * sûr en production tant qu'on ne configure pas explicitement cette variable).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestDelegueBootstrap implements ApplicationRunner {

    private final UserService    userService;
    private final DelegueService delegueService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${test.delegue.email:delegue.test@medisync.tn}")
    private String delegueEmail;

    @Value("${test.delegue.password:}")
    private String deleguePassword;

    @Override
    public void run(ApplicationArguments args) {
        if (deleguePassword == null || deleguePassword.isBlank()) {
            return;
        }
        User user;
        if (userService.existsByEmail(delegueEmail)) {
            user = userService.getByEmail(delegueEmail);
        } else {
            user = userService.save(User.builder()
                    .email(delegueEmail)
                    .passwordHash(passwordEncoder.encode(deleguePassword))
                    .role(RoleEnum.DELEGUE)
                    .mustChangePassword(false)
                    .build());
            log.info("[Profils] Utilisateur délégué de test créé : {}", delegueEmail);
        }

        try {
            delegueService.getByUserId(user.getId());
        } catch (ResourceNotFoundException e) {
            delegueService.create(user.getId(), null, "Test", "Délégué", "+216 00 000 000");
            log.info("[Profils] Profil délégué de test créé pour {}", delegueEmail);
        }
    }
}
