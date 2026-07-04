package com.project.medisync.modules.profils.repository;

import com.project.medisync.modules.profils.entity.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, UUID> {

    Optional<Medecin> findByIdAndDeletedAtIsNull(UUID id);

    List<Medecin> findAllByDeletedAtIsNull();

    Optional<Medecin> findByUserIdAndDeletedAtIsNull(UUID userId);

    List<Medecin> findBySpecialiteContainingIgnoreCaseAndDeletedAtIsNull(String specialite);

    boolean existsByUserIdAndDeletedAtIsNull(UUID userId);
}
