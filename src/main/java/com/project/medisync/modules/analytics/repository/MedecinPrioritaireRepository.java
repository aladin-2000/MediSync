package com.project.medisync.modules.analytics.repository;

import com.project.medisync.modules.analytics.entity.MedecinPrioritaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedecinPrioritaireRepository extends JpaRepository<MedecinPrioritaire, UUID> {

    List<MedecinPrioritaire> findByLaboratoireId(UUID laboratoireId);

    Optional<MedecinPrioritaire> findByLaboratoireIdAndMedecinId(UUID laboratoireId, UUID medecinId);

    boolean existsByLaboratoireIdAndMedecinId(UUID laboratoireId, UUID medecinId);
}
