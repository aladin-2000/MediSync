package com.project.medisync.modules.disponibilites.repository;

import com.project.medisync.modules.disponibilites.entity.DisponibiliteHebdomadaire;
import com.project.medisync.modules.disponibilites.entity.JourSemaineEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisponibiliteHebdomadaireRepository extends JpaRepository<DisponibiliteHebdomadaire, UUID> {

    Optional<DisponibiliteHebdomadaire> findByIdAndDeletedAtIsNull(UUID id);

    List<DisponibiliteHebdomadaire> findByMedecinIdAndDeletedAtIsNull(UUID medecinId);

    List<DisponibiliteHebdomadaire> findByMedecinIdAndIsActiveTrueAndDeletedAtIsNull(UUID medecinId);

    List<DisponibiliteHebdomadaire> findByMedecinIdAndJourSemaineAndDeletedAtIsNull(
            UUID medecinId, JourSemaineEnum jour);

    /** Toutes les disponibilités actives — utilisé par le job de génération des créneaux. */
    List<DisponibiliteHebdomadaire> findByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Détecte un chevauchement de plages horaires pour un médecin un jour donné.
     *
     * Une plage [A, B] chevauche [C, D] si A < D et C < B.
     * On exclut l'id courant pour permettre la modification sans faux positif.
     */
    @Query("""
            SELECT COUNT(d) > 0 FROM DisponibiliteHebdomadaire d
            WHERE d.medecinId     = :medecinId
              AND d.jourSemaine   = :jour
              AND d.deletedAt     IS NULL
              AND (:excludeId IS NULL OR d.id <> :excludeId)
              AND d.heureDebut    < :heureFin
              AND :heureDebut     < d.heureFin
            """)
    boolean existsChevauchement(
            @Param("medecinId")  UUID medecinId,
            @Param("jour")       JourSemaineEnum jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin")   LocalTime heureFin,
            @Param("excludeId")  UUID excludeId
    );
}
