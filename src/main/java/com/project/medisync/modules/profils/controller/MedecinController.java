package com.project.medisync.modules.profils.controller;

import com.project.medisync.modules.profils.dto.CreateMedecinCompletRequest;
import com.project.medisync.modules.profils.dto.CreateMedecinRequest;
import com.project.medisync.modules.profils.dto.MedecinResponse;
import com.project.medisync.modules.profils.dto.UpdateMedecinRequest;
import com.project.medisync.modules.profils.service.MedecinService;
import com.project.medisync.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST gérant les opérations CRUD sur les profils des Médecins
 * dans le cadre du module Profils.
 */
@RestController
@RequestMapping("/medecins")
@RequiredArgsConstructor
public class MedecinController {

    private final MedecinService medecinService;

    /**
     * Crée un nouveau profil Médecin associé à un compte utilisateur existant.
     *
     * @param req DTO contenant les informations du médecin à créer
     * @return Les détails du profil médecin créé encapsulés dans un ApiResponse
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MedecinResponse>> create(@Valid @RequestBody CreateMedecinRequest req) {
        var medecin = medecinService.create(
                req.getUserId(), req.getNom(), req.getPrenom(), req.getSpecialite(),
                req.getAdresseCabinet(), req.getLatitude(), req.getLongitude(), req.getScoreFiabiliteMin());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Profil médecin créé avec succès.", MedecinResponse.from(medecin)));
    }

    /**
     * Admin : crée en un seul appel le compte (email + mot de passe) et le profil médecin.
     * Permet de créer rapidement des médecins de test depuis une page d'administration.
     *
     * @param req DTO contenant email, mot de passe et informations du médecin
     * @return Les détails du profil médecin créé encapsulés dans un ApiResponse
     */
    @PostMapping("/creer-medecin-complet")
    public ResponseEntity<ApiResponse<MedecinResponse>> creerMedecinComplet(
            @Valid @RequestBody CreateMedecinCompletRequest req) {
        var medecin = medecinService.creerMedecinComplet(
                req.getEmail(), req.getPassword(), req.getNom(), req.getPrenom(), req.getSpecialite(),
                req.getAdresseCabinet(), req.getLatitude(), req.getLongitude(), req.getScoreFiabiliteMin());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compte et profil médecin créés avec succès.", MedecinResponse.from(medecin)));
    }

    /**
     * Récupère la liste de tous les médecins actifs (non soft-deleted) de la plateforme.
     *
     * @return La liste de tous les médecins sous forme de DTO
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedecinResponse>>> getAll() {
        List<MedecinResponse> list = medecinService.getAll()
                .stream().map(MedecinResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    /**
     * Récupère un profil Médecin spécifique par son identifiant unique String.
     *
     * @param id L'identifiant unique String du médecin à récupérer
     * @return Les détails du profil médecin trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedecinResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(MedecinResponse.from(medecinService.getById(id))));
    }

    /**
     * Récupère la liste des médecins exerçant une spécialité médicale donnée.
     * Effectue une recherche insensible à la casse et partielle.
     *
     * @param specialite La spécialité médicale recherchée (ex: Cardiologue)
     * @return La liste des médecins correspondants
     */
    @GetMapping("/specialite/{specialite}")
    public ResponseEntity<ApiResponse<List<MedecinResponse>>> getBySpecialite(@PathVariable String specialite) {
        List<MedecinResponse> list = medecinService.getBySpecialite(specialite)
                .stream().map(MedecinResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    /**
     * Met à jour les informations du profil d'un médecin existant.
     *
     * @param id L'identifiant unique String du médecin à modifier
     * @param req DTO contenant les nouvelles valeurs pour les informations du médecin
     * @return Les détails du profil médecin mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedecinResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateMedecinRequest req) {
        var medecin = medecinService.update(
                id, req.getNom(), req.getPrenom(), req.getSpecialite(),
                req.getAdresseCabinet(), req.getLatitude(), req.getLongitude(), req.getScoreFiabiliteMin());
        return ResponseEntity.ok(ApiResponse.ok("Profil médecin mis à jour.", MedecinResponse.from(medecin)));
    }

    /**
     * Supprime de façon logique (soft delete) un médecin de la plateforme.
     *
     * @param id L'identifiant unique String du médecin à soft-delete
     * @return Un message indiquant le succès de la suppression logique
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        medecinService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Profil médecin supprimé.", null));
    }
}
