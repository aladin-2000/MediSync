package com.project.medisync.modules.reservations.entity;

import com.project.medisync.modules.disponibilites.entity.Creneau;
import com.project.medisync.modules.profils.entity.Delegue;
import com.project.medisync.modules.profils.entity.Medecin;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Rendez-vous entre un délégué médical et un médecin sur un créneau donné.
 *
 * <p><b>Règles métier :</b>
 * <ul>
 *   <li>Un délégué ne peut pas avoir deux rendez-vous au même moment.</li>
 *   <li>Un créneau ne peut être réservé qu'une seule fois.</li>
 *   <li>Si le médecin annule, {@code motifAnnulation} est obligatoire
 *       et une {@link PropositionRemplacement} est créée automatiquement.</li>
 * </ul>
 * </p>
 */
@Entity
@Table(name = "rendez_vous")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creneau_id", nullable = false)
    private Creneau creneau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegue_id", nullable = false)
    private Delegue delegue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private StatutRendezVousEnum statut = StatutRendezVousEnum.CONFIRME;

    /** Renseigné uniquement si le rendez-vous a été annulé. */
    @Enumerated(EnumType.STRING)
    @Column(name = "annule_par", length = 10)
    private AnnuleParEnum annulePar;

    /** Obligatoire si {@code annulePar == MEDECIN}. */
    @Column(name = "motif_annulation", columnDefinition = "TEXT")
    private String motifAnnulation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Soft delete. */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
