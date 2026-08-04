package com.project.medisync.modules.reservations.entity;

/**
 * Statut d'un rendez-vous entre un délégué médical et un médecin.
 */
public enum StatutRendezVousEnum {
    RESERVE,
    ANNULE,
    REALISE,
    ABSENT_MEDECIN,
    ABSENT_DELEGUE
}
