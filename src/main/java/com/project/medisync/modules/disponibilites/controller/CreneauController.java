package com.project.medisync.modules.disponibilites.controller;

import com.project.medisync.modules.disponibilites.dto.CreneauRequest;
import com.project.medisync.modules.disponibilites.dto.CreneauResponse;
import com.project.medisync.modules.disponibilites.service.CreneauService;
import com.project.medisync.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medecins/{medecinId}/creneaux")
@RequiredArgsConstructor
public class CreneauController {

    private final CreneauService creneauService;

    /** POST /api/medecins/{medecinId}/creneaux — Création manuelle d'un créneau ponctuel */
    @PostMapping
    public ResponseEntity<ApiResponse<CreneauResponse>> create(
            @PathVariable String medecinId,
            @Valid @RequestBody CreneauRequest request) {

        var creneau = creneauService.createCreneau(
                medecinId,
                request.date(),
                request.heureDebut());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "Créneau créé avec succès.",
                        CreneauResponse.from(creneau)));
    }

    /**
     * GET /api/medecins/{medecinId}/creneaux?semaine=2025-06-30
     * Retourne tous les créneaux d'un médecin pour la semaine
     * contenant la date fournie (lundi → dimanche).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CreneauResponse>>> getBySemaine(
            @PathVariable String medecinId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate semaine) {

        // Si pas de date fournie → semaine en cours
        LocalDate lundi = (semaine != null ? semaine : LocalDate.now())
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        List<CreneauResponse> list = creneauService.getBySemaine(medecinId, lundi)
                .stream().map(CreneauResponse::from).toList();

        return ResponseEntity.ok(ApiResponse.ok(
                "Créneaux de la semaine du " + lundi + " récupérés.", list));
    }

    /** GET /api/medecins/{medecinId}/creneaux/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CreneauResponse>> getById(
            @PathVariable String medecinId,
            @PathVariable String id) {

        return ResponseEntity.ok(ApiResponse.ok(
                "Créneau récupéré.",
                CreneauResponse.from(creneauService.getById(id))));
    }

    /**
     * GET /api/medecins/{medecinId}/creneaux/disponibles-remplacement
     * Retourne les créneaux disponibles du médecin (semaine en cours + suivante).
     * Utilisé quand un médecin annule un RDV et doit proposer des alternatives.
     */
    @GetMapping("/disponibles-remplacement")
    public ResponseEntity<ApiResponse<List<CreneauResponse>>> getDisponiblesPourRemplacement(
            @PathVariable String medecinId) {

        List<CreneauResponse> list = creneauService.getDisponiblesPourRemplacement(medecinId)
                .stream().map(CreneauResponse::from).toList();

        return ResponseEntity.ok(ApiResponse.ok(
                "Créneaux disponibles pour remplacement récupérés.", list));
    }

    /** DELETE /api/medecins/{medecinId}/creneaux/{id} */
    /*@DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String medecinId,
            @PathVariable String id) {

        creneauService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Créneau supprimé.", null));
    }*/
}
