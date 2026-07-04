package com.project.medisync.modules.disponibilites.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Configuration des plages horaires récurrentes d'un médecin.
 *
 * <p>Cette table représente la "semaine type" du médecin.
 * Un job @Scheduled lit ces plages chaque vendredi soir et génère
 * les créneaux de 15 minutes correspondants pour la semaine suivante.</p>
 *
 * <p>Règle métier critique : deux plages du même médecin le même jour
 * ne peuvent pas se chevaucher. Cette contrainte est vérifiée au niveau
 * du service avant toute insertion ou modification.</p>
 *
 * <p>medecin_id est stocké comme UUID simple pour respecter l'isolation
 * modulaire : le module Disponibilités accède au médecin via
 * MedecinService (interface publique du module Profils), jamais via JPA.</p>
 */
@Entity
@Table(name = "disponibilite_hebdomadaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibiliteHebdomadaire {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID id;

    /**
     * Référence vers le médecin propriétaire (module Profils).
     * UUID uniquement — pas de @ManyToOne pour préserver l'isolation modulaire.
     */
    @Column(name = "medecin_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID medecinId;

    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine", nullable = false, length = 20)
    private JourSemaineEnum jourSemaine;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Soft delete. */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
