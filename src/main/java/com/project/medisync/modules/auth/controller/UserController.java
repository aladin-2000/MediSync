package com.project.medisync.modules.auth.controller;

import com.project.medisync.modules.auth.dto.CreateAdminRequest;
import com.project.medisync.modules.auth.dto.CreateUserRequest;
import com.project.medisync.modules.auth.dto.UserDetailResponse;
import com.project.medisync.modules.auth.dto.UserResponse;
import com.project.medisync.modules.auth.entity.RoleEnum;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.modules.profils.entity.Delegue;
import com.project.medisync.modules.profils.entity.Medecin;
import com.project.medisync.modules.profils.repository.DelegueRepository;
import com.project.medisync.modules.profils.repository.MedecinRepository;
import com.project.medisync.shared.dto.ApiResponse;
import com.project.medisync.shared.dto.PageResponse;
import com.project.medisync.shared.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Contrôleur REST gérant le cycle de vie et la gestion des utilisateurs (User)
 * dans le cadre du module Auth (Authentification).
 */
@RestController
@RequestMapping("/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MedecinRepository medecinRepository;
    private final DelegueRepository delegueRepository;

    /**
     * Crée un nouvel utilisateur en base de données.
     * Vérifie au préalable l'unicité de l'adresse email.
     *
     * @param req DTO contenant les informations requises (email, mot de passe en clair, rôle)
     * @return Une réponse encapsulée contenant les détails du compte créé (sans le mot de passe hashé)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest req) {
        if (userService.existsByEmail(req.getEmail())) {
            throw new BusinessException("Un compte existe déjà avec l'adresse email : " + req.getEmail());
        }
        User user = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .emailVerified(true)
                .build();
        User saved = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Utilisateur créé avec succès.", UserResponse.from(saved)));
    }

    /**
     * Crée directement un compte ADMIN (email + mot de passe, rôle forcé à ADMIN).
     */
    @PostMapping("creer-admin")
    public ResponseEntity<ApiResponse<UserResponse>> creerAdmin(@Valid @RequestBody CreateAdminRequest req) {
        if (userService.existsByEmail(req.getEmail())) {
            throw new BusinessException("Un compte existe déjà avec l'adresse email : " + req.getEmail());
        }
        User admin = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(RoleEnum.ADMIN)
                .emailVerified(true)
                .build();
        User saved = userService.save(admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Administrateur créé avec succès.", UserResponse.from(saved)));
    }

    @PostMapping("load-users")
    public ResponseEntity<ApiResponse<String>> seedDoctors() {

        List<CreateUserRequest> users = List.of(
                create("ahmed.benali@medisync.tn", "Ahmed123!", RoleEnum.MEDECIN),
                create("sarra.trabelsi@medisync.tn", "Sarra123!", RoleEnum.MEDECIN),
                create("mohamed.garbi@medisync.tn", "Mohamed123!", RoleEnum.MEDECIN),
                create("ines.benamor@medisync.tn", "Ines123!", RoleEnum.MEDECIN),
                create("youssef.cherif@medisync.tn", "Youssef123!", RoleEnum.MEDECIN),
                create("rim.mansouri@medisync.tn", "Rim123!", RoleEnum.MEDECIN),
                create("walid.jaziri@medisync.tn", "Walid123!", RoleEnum.MEDECIN),
                create("amira.mahmoudi@medisync.tn", "Amira123!", RoleEnum.MEDECIN),
                create("nour.bouzid@medisync.tn", "Nour123!", RoleEnum.MEDECIN),
                create("karim.guesmi@medisync.tn", "Karim123!", RoleEnum.MEDECIN),
                create("salma.kefi@medisync.tn", "Salma123!", RoleEnum.MEDECIN),
                create("aymen.souissi@medisync.tn", "Aymen123!", RoleEnum.MEDECIN),
                create("marwa.brahmi@medisync.tn", "Marwa123!", RoleEnum.MEDECIN),
                create("hatem.jemai@medisync.tn", "Hatem123!", RoleEnum.MEDECIN),
                create("fares.zouari@medisync.tn", "Fares123!", RoleEnum.MEDECIN),
                create("ons.kallel@medisync.tn", "Ons123!", RoleEnum.MEDECIN),
                create("sofiene.mezghani@medisync.tn", "Sofiene123!", RoleEnum.MEDECIN),
                create("dorra.belhaj@medisync.tn", "Dorra123!", RoleEnum.MEDECIN),
                create("anis.haddad@medisync.tn", "Anis123!", RoleEnum.MEDECIN),
                create("leila.sassi@medisync.tn", "Leila123!", RoleEnum.MEDECIN),

                create("ali.bouzid@medisync.tn", "Ali123!", RoleEnum.DELEGUE),
                create("marwen.kefi@medisync.tn", "Marwen123!", RoleEnum.DELEGUE),
                create("hichem.souissi@medisync.tn", "Hichem123!", RoleEnum.DELEGUE),
                create("nadia.brahmi@medisync.tn", "Nadia123!", RoleEnum.DELEGUE),
                create("samir.guesmi@medisync.tn", "Samir123!", RoleEnum.DELEGUE),
                create("amel.trabelsi@medisync.tn", "Amel123!", RoleEnum.DELEGUE),
                create("fathi.benamor@medisync.tn", "Fathi123!", RoleEnum.DELEGUE),
                create("rania.cherif@medisync.tn", "Rania123!", RoleEnum.DELEGUE),
                create("mehdi.jaziri@medisync.tn", "Mehdi123!", RoleEnum.DELEGUE),
                create("imen.mahmoudi@medisync.tn", "Imen123!", RoleEnum.DELEGUE),
                create("hamza.zouari@medisync.tn", "Hamza123!", RoleEnum.DELEGUE),
                create("ghada.kallel@medisync.tn", "Ghada123!", RoleEnum.DELEGUE),
                create("nabil.belhaj@medisync.tn", "Nabil123!", RoleEnum.DELEGUE),
                create("saber.sassi@medisync.tn", "Saber123!", RoleEnum.DELEGUE),
                create("wafa.haddad@medisync.tn", "Wafa123!", RoleEnum.DELEGUE),
                create("riadh.garbi@medisync.tn", "Riadh123!", RoleEnum.DELEGUE),
                create("mouna.mezghani@medisync.tn", "Mouna123!", RoleEnum.DELEGUE),
                create("yassine.khlifi@medisync.tn", "Yassine123!", RoleEnum.DELEGUE),
                create("olfa.benabdallah@medisync.tn", "Olfa123!", RoleEnum.DELEGUE),
                create("bilel.chaabane@medisync.tn", "Bilel123!", RoleEnum.DELEGUE),

                create("contact@pharmalab.tn", "Pharma123!", RoleEnum.LABO),
                create("contact@medilab.tn", "Medilab123!", RoleEnum.LABO),
                create("contact@biocare.tn", "Biocare123!", RoleEnum.LABO),
                create("contact@novapharma.tn", "Nova123!", RoleEnum.LABO),
                create("contact@unisante.tn", "Unisante123!", RoleEnum.LABO),
                create("contact@pharmatech.tn", "Pharmatech123!", RoleEnum.LABO),
                create("contact@tunislab.tn", "Tunislab123!", RoleEnum.LABO),
                create("contact@vitalis.tn", "Vitalis123!", RoleEnum.LABO),
                create("contact@genomed.tn", "Genomed123!", RoleEnum.LABO),
                create("contact@medpharma.tn", "Medpharma123!", RoleEnum.LABO),

                create("admin@medisync.tn", "Admin123!", RoleEnum.ADMIN),
                create("admin2@medisync.tn", "Admin456!", RoleEnum.ADMIN),
                create("superadmin@medisync.tn", "SuperAdmin123!", RoleEnum.ADMIN)
        );

        int inserted = 0;

        for (CreateUserRequest req : users) {

            if (userService.existsByEmail(req.getEmail())) {
                continue;
            }

            User user = User.builder()
                    .email(req.getEmail())
                    .passwordHash(passwordEncoder.encode(req.getPassword()))
                    .role(req.getRole())
                    .mustChangePassword(false) // comptes de démo/test, pas de vrai onboarding admin
                    .emailVerified(true)
                    .build();

            userService.save(user);
            inserted++;
        }

        return ResponseEntity.ok(
                ApiResponse.ok(inserted + " médecins insérés avec succès.")
        );
    }

    private CreateUserRequest create(String email, String password, RoleEnum role) {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail(email);
        req.setPassword(password);
        req.setRole(role);
        return req;
    }

    @GetMapping("liste")
    public ResponseEntity<ApiResponse<PageResponse<UserDetailResponse>>> getListe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) RoleEnum role,
            @RequestParam(required = false) String search) {

        Page<User> usersPage = userService.getMedecinsEtDelegues(role, search, PageRequest.of(page, size));
        List<User> users = usersPage.getContent();

        List<String> medecinUserIds = users.stream()
                .filter(u -> u.getRole() == RoleEnum.MEDECIN)
                .map(User::getId)
                .toList();
        List<String> delegueUserIds = users.stream()
                .filter(u -> u.getRole() == RoleEnum.DELEGUE)
                .map(User::getId)
                .toList();

        Map<String, Medecin> medecinsMap = medecinUserIds.isEmpty()
                ? Map.of()
                : medecinRepository.findByUserIdIn(medecinUserIds).stream()
                        .collect(Collectors.toMap(m -> m.getUser().getId(), m -> m));

        Map<String, Delegue> deleguesMap = delegueUserIds.isEmpty()
                ? Map.of()
                : delegueRepository.findByUserIdIn(delegueUserIds).stream()
                        .collect(Collectors.toMap(d -> d.getUser().getId(), d -> d));

        List<UserDetailResponse> dtos = users.stream().map(user -> {
            String nom = "";
            String prenom = "";
            if (user.getRole() == RoleEnum.MEDECIN) {
                Medecin m = medecinsMap.get(user.getId());
                if (m != null) { nom = m.getNom(); prenom = m.getPrenom(); }
            } else {
                Delegue d = deleguesMap.get(user.getId());
                if (d != null) { nom = d.getNom(); prenom = d.getPrenom(); }
            }
            return UserDetailResponse.from(user, nom, prenom);
        }).toList();

        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(usersPage, dtos)));
    }

    @PatchMapping("{id}/activer")
    public ResponseEntity<ApiResponse<UserDetailResponse>> activer(@PathVariable String id) {
        User user = userService.activer(id);
        String nom = "";
        String prenom = "";
        if (user.getRole() == RoleEnum.MEDECIN) {
            Medecin m = medecinRepository.findByUserId(id).orElse(null);
            if (m != null) { nom = m.getNom(); prenom = m.getPrenom(); }
        } else if (user.getRole() == RoleEnum.DELEGUE) {
            Delegue d = delegueRepository.findByUserId(id).orElse(null);
            if (d != null) { nom = d.getNom(); prenom = d.getPrenom(); }
        }
        return ResponseEntity.ok(ApiResponse.ok("Utilisateur activé.", UserDetailResponse.from(user, nom, prenom)));
    }

    @PatchMapping("{id}/desactiver")
    public ResponseEntity<ApiResponse<UserDetailResponse>> desactiver(@PathVariable String id) {
        User user = userService.desactiver(id);
        String nom = "";
        String prenom = "";
        if (user.getRole() == RoleEnum.MEDECIN) {
            Medecin m = medecinRepository.findByUserId(id).orElse(null);
            if (m != null) { nom = m.getNom(); prenom = m.getPrenom(); }
        } else if (user.getRole() == RoleEnum.DELEGUE) {
            Delegue d = delegueRepository.findByUserId(id).orElse(null);
            if (d != null) { nom = d.getNom(); prenom = d.getPrenom(); }
        }
        return ResponseEntity.ok(ApiResponse.ok("Utilisateur désactivé.", UserDetailResponse.from(user, nom, prenom)));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<UserResponse>> getById(@RequestParam  String id) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.getById(id))));
    }

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<UserResponse>> getByUserEmail(@RequestParam  String email) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.getByEmail(email))));
    }

    /**
     * Supprime de façon logique (soft delete) un utilisateur de la plateforme.
     *
     * @param id L'identifiant unique String de l'utilisateur à désactiver/soft delete
     * @return Un message indiquant le succès de la suppression logique
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Utilisateur supprimé.", null));
    }
}
