package com.project.medisync.modules.auth.controller;

import com.project.medisync.modules.auth.dto.CreateUserRequest;
import com.project.medisync.modules.auth.dto.UserResponse;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.shared.dto.ApiResponse;
import com.project.medisync.shared.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Contrôleur REST gérant le cycle de vie et la gestion des utilisateurs (User)
 * dans le cadre du module Auth (Authentification).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
                .passwordHash(req.getPassword()) // TODO : encoder via BCrypt (phase sécurité)
                .role(req.getRole())
                .build();
        User saved = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Utilisateur créé avec succès.", UserResponse.from(saved)));
    }

    /**
     * Récupère un utilisateur spécifique par son identifiant unique UUID.
     *
     * @param id L'identifiant unique UUID de l'utilisateur à récupérer
     * @return Les détails de l'utilisateur trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.getById(id))));
    }

    /**
     * Supprime de façon logique (soft delete) un utilisateur de la plateforme.
     *
     * @param id L'identifiant unique UUID de l'utilisateur à désactiver/soft delete
     * @return Un message indiquant le succès de la suppression logique
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Utilisateur supprimé.", null));
    }
}
