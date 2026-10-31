package com.project.medisync.modules.profils.repository;

import com.project.medisync.modules.profils.entity.Laboratoire;
import com.project.medisync.modules.profils.entity.StatutAbonnementEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratoireRepository extends JpaRepository<Laboratoire, String> {

    Optional<Laboratoire> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByNom(String nom);

    List<Laboratoire> findByDateFinAbonnementBeforeAndStatutAbonnementNot(
            LocalDate date, StatutAbonnementEnum statut);
}
