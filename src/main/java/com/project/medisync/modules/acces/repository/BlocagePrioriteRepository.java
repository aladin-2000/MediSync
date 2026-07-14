package com.project.medisync.modules.acces.repository;

import com.project.medisync.modules.acces.entity.BlocagePriorite;
import com.project.medisync.modules.acces.entity.TypeBlocageEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlocagePrioriteRepository extends JpaRepository<BlocagePriorite, String> {

    Optional<BlocagePriorite> findByMedecinIdAndDelegueId(String medecinId, String delegueId);

    List<BlocagePriorite> findByMedecinIdAndType(String medecinId, TypeBlocageEnum type);

    boolean existsByMedecinIdAndDelegueIdAndType(String medecinId, String delegueId, TypeBlocageEnum type);
}
