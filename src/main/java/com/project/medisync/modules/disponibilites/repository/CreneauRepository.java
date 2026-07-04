package com.project.medisync.modules.disponibilites.repository;

import com.project.medisync.modules.disponibilites.entity.Creneau;
import com.project.medisync.modules.disponibilites.entity.StatutCreneauEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreneauRepository extends JpaRepository<Creneau, UUID> {

    Optional<Creneau> findByIdAndDeletedAtIsNull(UUID id);

    /** Créneaux d'un médecin sur une période donnée. */
    List<Creneau> findByMedecinIdAndDateBetweenAndDeletedAtIsNull(
            UUID medecinId, LocalDate dateDebut, LocalDate dateFin);

    /** Créneaux disponibles d'un médecin sur une période — utilisé pour les propositions de remplacement. */
    List<Creneau> findByMedecinIdAndStatutAndDateBetweenAndDeletedAtIsNull(
            UUID medecinId, StatutCreneauEnum statut, LocalDate dateDebut, LocalDate dateFin);

    /** Vérifie si un créneau existe déjà pour éviter les doublons lors de la génération automatique. */
    boolean existsByMedecinIdAndDateAndHeureDebutAndDeletedAtIsNull(
            UUID medecinId, LocalDate date, java.time.LocalTime heureDebut);

    /**
     * Soft-delete en masse des créneaux DISPONIBLES futurs issus d'une règle de récurrence.
     * Appelé quand le médecin désactive ou supprime une DisponibiliteHebdomadaire.
     */
    @Modifying
    @Query("""
            UPDATE Creneau c SET c.deletedAt = CURRENT_TIMESTAMP
            WHERE c.disponibiliteHebdoId = :disponibiliteHebdoId
              AND c.statut               = 'DISPONIBLE'
              AND c.date                 >= :aujourdhui
              AND c.deletedAt            IS NULL
            """)
    void softDeleteFutursDisponibles(
            @Param("disponibiliteHebdoId") UUID disponibiliteHebdoId,
            @Param("aujourdhui")           LocalDate aujourdhui
    );
}
