package com.project.medisync.modules.profils.controller;

import com.project.medisync.modules.profils.dto.CreateDelegueRequest;
import com.project.medisync.modules.profils.dto.DelegueResponse;
import com.project.medisync.modules.profils.dto.UpdateDelegueRequest;
import com.project.medisync.modules.profils.service.DelegueService;
import com.project.medisync.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST gérant les opérations CRUD sur les profils des Délégués Médicaux
 * dans le cadre du module Profils.
 */
@RestController
@RequestMapping("/api/delegues")
@RequiredArgsConstructor
public class DelegueController {

    private final DelegueService delegueService;

    /**
     * Crée un nouveau profil Délégué associé à un compte utilisateur et à un laboratoire existants.
     *
     * @param req DTO contenant les informations du délégué à créer
     * @return Les détails du profil délégué créé encapsulés dans un ApiResponse
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DelegueResponse>> create(@Valid @RequestBody CreateDelegueRequest req) {
        var delegue = delegueService.create(
                req.getUserId(), req.getLaboratoireId(),
                req.getNom(), req.getPrenom(), req.getTelephone());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Profil délégué créé avec succès.", DelegueResponse.from(delegue)));
    }

    /**
     * Récupère la liste de tous les délégués médicaux actifs (non soft-deleted) de la plateforme.
     *
     * @return La liste de tous les délégués sous forme de DTO
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DelegueResponse>>> getAll() {
        List<DelegueResponse> list = delegueService.getAll()
                .stream().map(DelegueResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    /**
     * Récupère un profil Délégué spécifique par son identifiant unique String.
     *
     * @param id L'identifiant unique String du délégué à récupérer
     * @return Les détails du profil délégué trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DelegueResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(DelegueResponse.from(delegueService.getById(id))));
    }

    /**
     * Récupère le profil délégué (et donc son delegueId) à partir du userId du compte connecté.
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<DelegueResponse>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(DelegueResponse.from(delegueService.getByUserId(userId))));
    }

    /**
     * Récupère la liste des délégués médicaux rattachés à un laboratoire spécifique.
     *
     * @param laboratoireId L'identifiant unique String du laboratoire cible
     * @return La liste des délégués associés à ce laboratoire
     */
    @GetMapping("/laboratoire/{laboratoireId}")
    public ResponseEntity<ApiResponse<List<DelegueResponse>>> getByLaboratoire(@PathVariable String laboratoireId) {
        List<DelegueResponse> list = delegueService.getByLaboratoire(laboratoireId)
                .stream().map(DelegueResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    /**
     * Met à jour les informations du profil d'un délégué existant.
     *
     * @param id L'identifiant unique String du délégué à modifier
     * @param req DTO contenant les nouvelles valeurs pour les informations du délégué
     * @return Les détails du profil délégué mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DelegueResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody CreateDelegueRequest req) {
        var delegue = delegueService.update(id, req.getNom(), req.getPrenom(), req.getTelephone());
        return ResponseEntity.ok(ApiResponse.ok("Profil délégué mis à jour.", DelegueResponse.from(delegue)));
    }

    /**
     * Permet au délégué connecté de mettre à jour son propre profil (nom, prénom, téléphone).
     * Ne touche jamais à l'email/mot de passe, ni au laboratoire de rattachement.
     *
     * @param auth Authentification du délégué connecté (résout son userId depuis le JWT)
     * @param req  DTO contenant les nouvelles valeurs pour son profil
     * @return Les détails du profil délégué mis à jour
     */
    @PutMapping("/mon-profil")
    public ResponseEntity<ApiResponse<DelegueResponse>> updateMonProfil(
            Authentication auth,
            @Valid @RequestBody UpdateDelegueRequest req) {
        var delegueActuel = delegueService.getByUserId(auth.getName());
        var delegue = delegueService.update(delegueActuel.getId(), req.getNom(), req.getPrenom(), req.getTelephone());
        return ResponseEntity.ok(ApiResponse.ok("Profil mis à jour.", DelegueResponse.from(delegue)));
    }

    /**
     * Supprime de façon logique (soft delete) un délégué de la plateforme.
     *
     * @param id L'identifiant unique String du délégué à soft-delete
     * @return Un message indiquant le succès de la suppression logique
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        delegueService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Profil délégué supprimé.", null));
    }
}
