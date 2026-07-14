package com.project.medisync.modules.reservations.controller;

import com.project.medisync.modules.reservations.dto.AnnulationMedecinRequest;
import com.project.medisync.modules.reservations.dto.ReservationRequest;
import com.project.medisync.modules.reservations.dto.RendezVousResponse;
import com.project.medisync.modules.reservations.service.RendezVousService;
import com.project.medisync.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST gérant les opérations sur les Rendez-vous (réservations, annulations, réalisations).
 */
@RestController
@RequestMapping("/api/rendezvous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService rendezVousService;

    /**
     * Réserve un nouveau rendez-vous entre un délégué médical et un médecin sur un créneau donné.
     *
     * @param request Le DTO de requête contenant les identifiants requis (créneau, délégué, médecin).
     * @return Les détails du rendez-vous créé sous forme de RendezVousResponse enveloppé dans un ApiResponse.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RendezVousResponse>> reserver(
            @Valid @RequestBody ReservationRequest request) {
        
        var rdv = rendezVousService.reserver(
                request.creneauId(),
                request.delegueId(),
                request.medecinId()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Rendez-vous réservé avec succès.", RendezVousResponse.from(rdv)));
    }

    /**
     * Récupère les informations détaillées d'un rendez-vous par son identifiant unique.
     *
     * @param id L'identifiant unique String du rendez-vous à récupérer.
     * @return Les détails du rendez-vous trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RendezVousResponse>> getById(
            @PathVariable String id) {
        
        var rdv = rendezVousService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous récupéré avec succès.", RendezVousResponse.from(rdv)));
    }

    /**
     * Récupère la liste de tous les rendez-vous associés à un délégué médical.
     *
     * @param delegueId L'identifiant unique String du délégué médical.
     * @return La liste des rendez-vous correspondants.
     */
    @GetMapping("/delegue/{delegueId}")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> getByDelegue(
            @PathVariable String delegueId) {
        
        List<RendezVousResponse> list = rendezVousService.getByDelegue(delegueId)
                .stream()
                .map(RendezVousResponse::from)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous du délégué récupérés.", list));
    }

    /**
     * Récupère la liste de tous les rendez-vous associés à un médecin.
     *
     * @param medecinId L'identifiant unique String du médecin.
     * @return La liste des rendez-vous correspondants.
     */
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> getByMedecin(
            @PathVariable String medecinId) {
        
        List<RendezVousResponse> list = rendezVousService.getByMedecin(medecinId)
                .stream()
                .map(RendezVousResponse::from)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous du médecin récupérés.", list));
    }

    /**
     * Annule un rendez-vous à l'initiative du délégué médical.
     *
     * @param id L'identifiant unique String du rendez-vous à annuler.
     * @return Les détails du rendez-vous annulé.
     */
    @PatchMapping("/{id}/annuler-delegue")
    public ResponseEntity<ApiResponse<RendezVousResponse>> annulerParDelegue(
            @PathVariable String id) {
        
        var rdv = rendezVousService.annulerParDelegue(id);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous annulé avec succès par le délégué.", RendezVousResponse.from(rdv)));
    }

    /**
     * Annule un rendez-vous à l'initiative du médecin, avec justification obligatoire.
     *
     * @param id      L'identifiant unique String du rendez-vous à annuler.
     * @param request Le DTO contenant le motif d'annulation.
     * @return Les détails du rendez-vous annulé.
     */
    @PatchMapping("/{id}/annuler-medecin")
    public ResponseEntity<ApiResponse<RendezVousResponse>> annulerParMedecin(
            @PathVariable String id,
            @Valid @RequestBody AnnulationMedecinRequest request) {
        
        var rdv = rendezVousService.annulerParMedecin(id, request.motifAnnulation());
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous annulé avec succès par le médecin. Une proposition de remplacement a été générée.", RendezVousResponse.from(rdv)));
    }

    /**
     * Marque un rendez-vous comme réalisé.
     *
     * @param id L'identifiant unique String du rendez-vous.
     * @return Les détails du rendez-vous mis à jour.
     */
    @PatchMapping("/{id}/realise")
    public ResponseEntity<ApiResponse<RendezVousResponse>> marquerRealise(
            @PathVariable String id) {
        
        var rdv = rendezVousService.marquerRealise(id);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous marqué comme réalisé.", RendezVousResponse.from(rdv)));
    }

    /**
     * Marque le délégué médical comme absent pour le rendez-vous.
     *
     * @param id L'identifiant unique String du rendez-vous.
     * @return Les détails du rendez-vous mis à jour.
     */
    @PatchMapping("/{id}/absent")
    public ResponseEntity<ApiResponse<RendezVousResponse>> marquerAbsent(
            @PathVariable String id) {
        
        var rdv = rendezVousService.marquerAbsent(id);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous marqué avec absence du délégué.", RendezVousResponse.from(rdv)));
    }
}
