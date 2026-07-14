package com.project.medisync.modules.profils.repository;

import com.project.medisync.modules.profils.entity.Delegue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DelegueRepository extends JpaRepository<Delegue, String> {

    Optional<Delegue> findByIdAndDeletedAtIsNull(String id);

    List<Delegue> findAllByDeletedAtIsNull();

    List<Delegue> findByLaboratoireIdAndDeletedAtIsNull(String laboratoireId);

    Optional<Delegue> findByUserIdAndDeletedAtIsNull(String userId);

    boolean existsByUserIdAndDeletedAtIsNull(String userId);
}
