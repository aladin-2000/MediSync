package com.project.medisync.modules.analytics.repository;

import com.project.medisync.modules.analytics.entity.MedecinPrioritaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinPrioritaireRepository extends JpaRepository<MedecinPrioritaire, String> {

    List<MedecinPrioritaire> findByLaboratoireId(String laboratoireId);

    Optional<MedecinPrioritaire> findByLaboratoireIdAndMedecinId(String laboratoireId, String medecinId);

    boolean existsByLaboratoireIdAndMedecinId(String laboratoireId, String medecinId);
}
