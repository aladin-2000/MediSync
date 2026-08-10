package com.project.medisync.modules.profils.bootstrap;

import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.entity.SpecialiteEnum;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Crée 5 comptes Médecin de test au démarrage de l'application, s'ils n'existent pas encore.
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

    @Value("${test.medecin.password:}")
    private String medecinPassword;

    private record MedecinSeed(String email, String nom, String prenom, SpecialiteEnum specialite,
                                String adresseCabinet, String telephone, Double latitude, Double longitude) {}

    private static final List<MedecinSeed> MEDECINS = List.of(
            new MedecinSeed("ahmed.bensalah@medisync.tn", "Ben Salah", "Ahmed", SpecialiteEnum.CARDIOLOGIE,
                    "12 Avenue Habib Bourguiba, Tunis", "+216 71 200 101", 36.8065, 10.1815),
            new MedecinSeed("amina.trabelsi@medisync.tn", "Trabelsi", "Amina", SpecialiteEnum.DERMATOLOGIE,
                    "5 Rue de Marseille, Tunis", "+216 71 200 102", 36.8000, 10.1800),
            new MedecinSeed("karim.bouzid@medisync.tn", "Bouzid", "Karim", SpecialiteEnum.PEDIATRIE,
                    "18 Avenue Mohamed V, Sfax", "+216 74 200 103", 34.7406, 10.7603),
            new MedecinSeed("sonia.gharbi@medisync.tn", "Gharbi", "Sonia", SpecialiteEnum.GYNECOLOGIE,
                    "7 Rue Ibn Khaldoun, Sousse", "+216 73 200 104", 35.8256, 10.6084),
            new MedecinSeed("yassine.chaabane@medisync.tn", "Chaabane", "Yassine", SpecialiteEnum.MEDECINE_GENERALE,
                    "22 Avenue de la République, Ariana", "+216 71 200 105", 36.8625, 10.1956)
    );

    @Override
    public void run(ApplicationArguments args) {
        if (medecinPassword == null || medecinPassword.isBlank()) {
            return;
        }

        for (MedecinSeed seed : MEDECINS) {
            User user;
            if (userService.existsByEmail(seed.email())) {
                user = userService.getByEmail(seed.email());
            } else {
                user = userService.save(User.builder()
                        .email(seed.email())
                        .passwordHash(passwordEncoder.encode(medecinPassword))
                        .role(RoleEnum.MEDECIN)
                        .mustChangePassword(false)
                        .build());
                log.info("[Profils] Utilisateur médecin de test créé : {}", seed.email());
            }

            try {
                medecinService.getByUserId(user.getId());
            } catch (ResourceNotFoundException e) {
                medecinService.create(user.getId(), seed.nom(), seed.prenom(), seed.specialite(),
                        seed.adresseCabinet(), seed.telephone(), seed.latitude(), seed.longitude(), 0f);
                log.info("[Profils] Profil médecin de test créé pour {}", seed.email());
            }
        }
    }
}
