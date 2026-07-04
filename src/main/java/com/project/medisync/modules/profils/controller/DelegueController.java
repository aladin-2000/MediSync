package com.project.medisync.modules.profils.controller;

import com.project.medisync.modules.profils.dto.CreateDelegueRequest;
import com.project.medisync.modules.profils.dto.DelegueResponse;
import com.project.medisync.modules.profils.service.DelegueService;
import com.project.medisync.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
     * Récupère un profil Délégué spécifique par son identifiant unique UUID.
     *
     * @param id L'identifiant unique UUID du délégué à récupérer
     * @return Les détails du profil délégué trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DelegueResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(DelegueResponse.from(delegueService.getById(id))));
    }

    /**
     * Récupère la liste des délégués médicaux rattachés à un laboratoire spécifique.
     *
     * @param laboratoireId L'identifiant unique UUID du laboratoire cible
     * @return La liste des délégués associés à ce laboratoire
     */
    @GetMapping("/laboratoire/{laboratoireId}")
    public ResponseEntity<ApiResponse<List<DelegueResponse>>> getByLaboratoire(@PathVariable UUID laboratoireId) {
        List<DelegueResponse> list = delegueService.getByLaboratoire(laboratoireId)
                .stream().map(DelegueResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    /**
     * Met à jour les informations du profil d'un délégué existant.
     *
     * @param id L'identifiant unique UUID du délégué à modifier
     * @param req DTO contenant les nouvelles valeurs pour les informations du délégué
     * @return Les détails du profil délégué mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DelegueResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDelegueRequest req) {
        var delegue = delegueService.update(id, req.getNom(), req.getPrenom(), req.getTelephone());
        return ResponseEntity.ok(ApiResponse.ok("Profil délégué mis à jour.", DelegueResponse.from(delegue)));
    }

    /**
     * Supprime de façon logique (soft delete) un délégué de la plateforme.
     *
     * @param id L'identifiant unique UUID du délégué à soft-delete
     * @return Un message indiquant le succès de la suppression logique
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        delegueService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Profil délégué supprimé.", null));
    }
}
