package com.project.medisync.modules.reservations.repository;

import com.project.medisync.modules.reservations.entity.RendezVous;
import com.project.medisync.modules.reservations.entity.StatutRendezVousEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, String> {

    Optional<RendezVous> findByIdAndDeletedAtIsNull(String id);

    List<RendezVous> findByDelegueIdAndDeletedAtIsNull(String delegueId);

    List<RendezVous> findByMedecinIdAndDeletedAtIsNull(String medecinId);

    /** Vérifie qu'un créneau n'est pas déjà réservé. */
    boolean existsByCreneauIdAndStatutNotAndDeletedAtIsNull(String creneauId, StatutRendezVousEnum statut);

    /**
     * Vérifie le chevauchement horaire pour un délégué :
     * un délégué ne peut pas avoir deux RDV au même moment.
     */
    @Query("""
            SELECT COUNT(r) > 0 FROM RendezVous r
            WHERE r.delegue.id = :delegueId
              AND r.creneau.date = (SELECT c.date FROM Creneau c WHERE c.id = :creneauId)
              AND r.creneau.heureDebut = (SELECT c.heureDebut FROM Creneau c WHERE c.id = :creneauId)
              AND r.statut <> 'ANNULE'
              AND r.deletedAt IS NULL
            """)
    boolean existsByDelegueConflict(@Param("delegueId") String delegueId,
                                    @Param("creneauId") String creneauId);
}
