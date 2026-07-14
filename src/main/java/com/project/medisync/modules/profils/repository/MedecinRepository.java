package com.project.medisync.modules.profils.repository;

import com.project.medisync.modules.profils.entity.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, String> {

    Optional<Medecin> findByIdAndDeletedAtIsNull(String id);

    List<Medecin> findAllByDeletedAtIsNull();

    Optional<Medecin> findByUserIdAndDeletedAtIsNull(String userId);

    List<Medecin> findBySpecialiteContainingIgnoreCaseAndDeletedAtIsNull(String specialite);

    boolean existsByUserIdAndDeletedAtIsNull(String userId);
}
