package com.project.medisync.modules.reservations.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Représente la requête de réservation d'un créneau par un délégué médical avec un médecin.
 */
public record ReservationRequest(
        /** Identifiant unique du créneau à réserver. */
        @NotNull(message = "L'identifiant du créneau est obligatoire.")
        UUID creneauId,

        /** Identifiant unique du délégué effectuant la réservation. */
        @NotNull(message = "L'identifiant du délégué est obligatoire.")
        UUID delegueId,

        /** Identifiant unique du médecin concerné par la réservation. */
        @NotNull(message = "L'identifiant du médecin est obligatoire.")
        UUID medecinId
) {}
