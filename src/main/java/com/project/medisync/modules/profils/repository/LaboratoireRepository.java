package com.project.medisync.modules.profils.repository;

import com.project.medisync.modules.profils.entity.Laboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratoireRepository extends JpaRepository<Laboratoire, String> {

    Optional<Laboratoire> findByIdAndDeletedAtIsNull(String id);

    List<Laboratoire> findAllByDeletedAtIsNull();

    Optional<Laboratoire> findByUserIdAndDeletedAtIsNull(String userId);

    boolean existsByUserIdAndDeletedAtIsNull(String userId);
}
