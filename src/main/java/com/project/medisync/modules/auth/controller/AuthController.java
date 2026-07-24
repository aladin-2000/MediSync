package com.project.medisync.modules.auth.controller;

import com.project.medisync.modules.auth.dto.AuthResponse;
import com.project.medisync.modules.auth.dto.LoginRequest;
import com.project.medisync.modules.auth.dto.UserResponse;
import com.project.medisync.modules.auth.entity.User;
import com.project.medisync.modules.auth.security.JwtUtil;
import com.project.medisync.modules.auth.service.UserService;
import com.project.medisync.shared.dto.ApiResponse;
import com.project.medisync.shared.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentification par JWT : le login renvoie un token, à renvoyer ensuite
 * dans le header "Authorization: Bearer <token>" sur les requêtes protégées.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Vérifie l'email et le mot de passe, renvoie un token JWT + les infos de l'utilisateur.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {

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

        String token = jwtUtil.genererToken(user.getId(), user.getRole().name());
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .user(UserResponse.from(user))
                .build();

        return ResponseEntity.ok(ApiResponse.ok("Connexion réussie.", response));
    }

    /**
     * Revalide le token et renvoie les infos de l'utilisateur connecté.
     * Utilisé par le front au démarrage de l'app pour vérifier si le token est toujours valide.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        String userId = authentication.getName();
        User user = userService.getById(userId);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(user)));
    }
}
