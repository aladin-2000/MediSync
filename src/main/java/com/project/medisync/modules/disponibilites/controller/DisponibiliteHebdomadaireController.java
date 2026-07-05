package com.project.medisync.modules.disponibilites.controller;

import com.project.medisync.modules.disponibilites.dto.DisponibiliteHebdomadaireRequest;
import com.project.medisync.modules.disponibilites.dto.DisponibiliteHebdomadaireResponse;
import com.project.medisync.modules.disponibilites.service.DisponibiliteHebdomadaireService;
import com.project.medisync.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medecins/{medecinId}/disponibilites")
@RequiredArgsConstructor
public class DisponibiliteHebdomadaireController {

    private final DisponibiliteHebdomadaireService disponibiliteService;

    /** POST /api/medecins/{medecinId}/disponibilites */
    @PostMapping
    public ResponseEntity<ApiResponse<DisponibiliteHebdomadaireResponse>> create(
            @PathVariable UUID medecinId,
            @Valid @RequestBody DisponibiliteHebdomadaireRequest request) {

        var dispo = disponibiliteService.create(
                medecinId,
                request.jourSemaine(),
                request.heureDebut(),
                request.heureFin());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "Disponibilité créée avec succès.",
                        DisponibiliteHebdomadaireResponse.from(dispo)));
    }

    /** GET /api/medecins/{medecinId}/disponibilites */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DisponibiliteHebdomadaireResponse>>> getAll(
            @PathVariable UUID medecinId,
            @RequestParam(defaultValue = "false") boolean actifSeulement) {

        List<DisponibiliteHebdomadaireResponse> list = actifSeulement
                ? disponibiliteService.getActivesByMedecin(medecinId).stream()
                        .map(DisponibiliteHebdomadaireResponse::from).toList()
                : disponibiliteService.getByMedecin(medecinId).stream()
                        .map(DisponibiliteHebdomadaireResponse::from).toList();

        return ResponseEntity.ok(ApiResponse.ok("Disponibilités récupérées.", list));
    }

    /** GET /api/medecins/{medecinId}/disponibilites/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DisponibiliteHebdomadaireResponse>> getById(
            @PathVariable UUID medecinId,
            @PathVariable UUID id) {

        return ResponseEntity.ok(ApiResponse.ok(
                "Disponibilité récupérée.",
                DisponibiliteHebdomadaireResponse.from(disponibiliteService.getById(id))));
    }

    /** PUT /api/medecins/{medecinId}/disponibilites/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DisponibiliteHebdomadaireResponse>> update(
            @PathVariable UUID medecinId,
            @PathVariable UUID id,
            @Valid @RequestBody DisponibiliteHebdomadaireRequest request) {

        var updated = disponibiliteService.update(
                id,
                request.jourSemaine(),
                request.heureDebut(),
                request.heureFin());

        return ResponseEntity.ok(ApiResponse.ok(
                "Disponibilité mise à jour.",
                DisponibiliteHebdomadaireResponse.from(updated)));
    }

    /** PATCH /api/medecins/{medecinId}/disponibilites/{id}/toggle?active=false */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggle(
            @PathVariable UUID medecinId,
            @PathVariable UUID id,
            @RequestParam boolean active) {

        disponibiliteService.toggleActive(id, active);
        String msg = active ? "Disponibilité activée." : "Disponibilité désactivée — créneaux futurs supprimés.";
        return ResponseEntity.ok(ApiResponse.ok(msg, null));
    }

    /** DELETE /api/medecins/{medecinId}/disponibilites/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID medecinId,
            @PathVariable UUID id) {

        disponibiliteService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(
                "Disponibilité supprimée — créneaux futurs annulés.", null));
    }
}
