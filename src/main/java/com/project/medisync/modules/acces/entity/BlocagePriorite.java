package com.project.medisync.modules.acces.entity;

import com.project.medisync.modules.profils.entity.Delegue;
import com.project.medisync.modules.profils.entity.Medecin;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Règle d'accès prioritaire définie par un médecin sur un délégué.
 *
 * <p>Un médecin peut bloquer ({@code BLOQUE}) ou accorder un accès prioritaire
 * ({@code ACCESSIBLE}) à un délégué spécifique.</p>
 */
@Entity
@Table(name = "blocage_priorite",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_blocage_medecin_delegue",
                columnNames = {"medecin_id", "delegue_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlocagePriorite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegue_id", nullable = false)
    private Delegue delegue;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TypeBlocageEnum type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
