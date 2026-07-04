package com.project.medisync.modules.disponibilites.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Instance concrète de disponibilité d'un médecin.
 *
 * <p>La durée de chaque créneau est fixée à 15 minutes.
 * Il n'y a pas de champ heure_fin — elle se déduit toujours de heureDebut + 15 min.</p>
 *
 * <p>Les créneaux sont soit :
 * <ul>
 *   <li>Générés automatiquement par le job @Scheduled depuis DisponibiliteHebdomadaire
 *       → disponibiliteHebdoId renseigné</li>
 *   <li>Créés manuellement par le médecin (ponctuel)
 *       → disponibiliteHebdoId = null</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "creneau")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID id;

    /**
     * Référence vers le médecin propriétaire (module Profils).
     * UUID uniquement — isolation modulaire.
     */
    @Column(name = "medecin_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID medecinId;

    /**
     * Référence vers la règle de récurrence ayant généré ce créneau.
     * Null si le créneau a été créé manuellement par le médecin.
     * Permet de supprimer en masse les créneaux futurs si une règle est désactivée.
     */
    @Column(name = "disponibilite_hebdo_id", columnDefinition = "VARCHAR(36)")
    private UUID disponibiliteHebdoId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    /** Heure de début du créneau. La fin = heureDebut + 15 minutes. */
    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private StatutCreneauEnum statut = StatutCreneauEnum.DISPONIBLE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Soft delete. */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
