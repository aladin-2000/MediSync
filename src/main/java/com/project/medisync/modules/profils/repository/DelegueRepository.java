package com.project.medisync.modules.profils.repository;

import com.project.medisync.modules.profils.entity.Delegue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DelegueRepository extends JpaRepository<Delegue, UUID> {

    Optional<Delegue> findByIdAndDeletedAtIsNull(UUID id);

    List<Delegue> findAllByDeletedAtIsNull();

    List<Delegue> findByLaboratoireIdAndDeletedAtIsNull(UUID laboratoireId);

    Optional<Delegue> findByUserIdAndDeletedAtIsNull(UUID userId);

    boolean existsByUserIdAndDeletedAtIsNull(UUID userId);
}
