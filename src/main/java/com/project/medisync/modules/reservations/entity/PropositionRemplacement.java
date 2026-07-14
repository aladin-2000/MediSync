package com.project.medisync.modules.reservations.entity;

import com.project.medisync.modules.profils.entity.Delegue;
import com.project.medisync.modules.profils.entity.Medecin;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Proposition de remplacement créée automatiquement lorsqu'un médecin
 * annule un rendez-vous confirmé.
 *
 * <p><b>Cycle de vie :</b>
 * <ol>
 *   <li>Médecin annule → {@code statut = EN_ATTENTE}</li>
 *   <li>Délégué choisit un nouveau créneau → {@code statut = ACCEPTE},
 *       {@code rendezVousNouveau} est renseigné.</li>
 *   <li>Délai dépassé sans choix → {@code statut = EXPIRE} (via job @Scheduled).</li>
 * </ol>
 * </p>
 */
@Entity
@Table(name = "proposition_remplacement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropositionRemplacement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    /** Rendez-vous annulé à l'origine de cette proposition. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_annule_id", nullable = false)
    private RendezVous rendezVousAnnule;

    /** Nouveau rendez-vous accepté par le délégué — null tant que non accepté. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_nouveau_id")
    private RendezVous rendezVousNouveau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegue_id", nullable = false)
    private Delegue delegue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private StatutPropositionEnum statut = StatutPropositionEnum.EN_ATTENTE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
