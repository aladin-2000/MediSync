package com.project.medisync.modules.acces.repository;

import com.project.medisync.modules.acces.entity.BlocagePriorite;
import com.project.medisync.modules.acces.entity.TypeBlocageEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlocagePrioriteRepository extends JpaRepository<BlocagePriorite, UUID> {

    Optional<BlocagePriorite> findByMedecinIdAndDelegueId(UUID medecinId, UUID delegueId);

    List<BlocagePriorite> findByMedecinIdAndType(UUID medecinId, TypeBlocageEnum type);

    boolean existsByMedecinIdAndDelegueIdAndType(UUID medecinId, UUID delegueId, TypeBlocageEnum type);
}
