package com.project.medisync.modules.profils.repository;

import com.project.medisync.modules.profils.entity.Medecin;
import com.project.medisync.modules.profils.entity.SpecialiteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, String> {

    Optional<Medecin> findById(String id);

    Optional<Medecin> findByUserId(String userId);

    /** Médecins visibles du délégué (valide=true) ayant la spécialité donnée. */
    List<Medecin> findBySpecialiteAndValideTrue(SpecialiteEnum specialite);

    /** Médecins auto-inscrits en attente de validation par un admin. */
    List<Medecin> findByValideFalse();

    /**
     * Recherche par nom OU prénom (partielle, insensible à la casse) et spécialités (parmi une
     * liste, optionnelle) au sein d'une liste d'ids donnée. Si specialites est null ou vide, ne
     * filtre pas par spécialité. Ne retourne que les médecins validés (valide=true) — un médecin
     * auto-inscrit en attente de validation admin n'apparaît pas dans les recherches du délégué.
     */
    @Query("""
            SELECT m FROM Medecin m
            WHERE m.id IN :ids
              AND m.valide = true
              AND (LOWER(m.nom) LIKE LOWER(CONCAT('%', :nom, '%'))
                   OR LOWER(m.prenom) LIKE LOWER(CONCAT('%', :nom, '%')))
              AND (:specialites IS NULL OR m.specialite IN :specialites)
            ORDER BY m.nom ASC, m.prenom ASC
            """)
    List<Medecin> searchByIdsNomSpecialites(
            @Param("ids") List<String> ids,
            @Param("nom") String nom,
            @Param("specialites") List<SpecialiteEnum> specialites);

    boolean existsByUserId(String userId);
}
