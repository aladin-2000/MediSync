package com.project.medisync.modules.auth.controller;

import com.project.medisync.modules.auth.dto.LoginRequest;
import com.project.medisync.modules.auth.dto.UserResponse;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.shared.dto.ApiResponse;
import com.project.medisync.shared.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentification simple, sans token : le front récupère les infos
 * de l'utilisateur (dont son rôle) et les garde en mémoire côté client.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Vérifie l'email et le mot de passe, renvoie les infos de l'utilisateur (dont le rôle).
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@Valid @RequestBody LoginRequest req) {

        if (!userService.existsByEmail(req.getEmail())) {
            throw new BusinessException("Email ou mot de passe incorrect.");
        }

        User user = userService.getByEmail(req.getEmail());

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("Email ou mot de passe incorrect.");
        }
        if (!user.getIsActive()) {
            throw new BusinessException("Ce compte est désactivé.");
        }

        return ResponseEntity.ok(ApiResponse.ok("Connexion réussie.", UserResponse.from(user)));
    }
}
