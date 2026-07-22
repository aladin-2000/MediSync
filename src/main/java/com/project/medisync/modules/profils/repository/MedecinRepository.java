package com.project.medisync.modules.profils.repository;

import com.project.medisync.modules.profils.entity.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, String> {

    Optional<Medecin> findById(String id);

    Optional<Medecin> findByUserId(String userId);

    List<Medecin> findBySpecialiteContainingIgnoreCase(String specialite);

    /** Recherche par nom + spécialité (recherche partielle) parmi une liste d'ids donnée. */
    List<Medecin> findByIdInAndNomContainingIgnoreCaseAndSpecialiteContainingIgnoreCase(
            List<String> ids, String nom, String specialite);

    boolean existsByUserId(String userId);
}
