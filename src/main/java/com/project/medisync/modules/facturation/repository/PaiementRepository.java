package com.project.medisync.modules.facturation.repository;

import com.project.medisync.modules.facturation.entity.Paiement;
import com.project.medisync.modules.facturation.entity.StatutPaiementEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, UUID> {

    Optional<Paiement> findByIdAndDeletedAtIsNull(UUID id);

    List<Paiement> findByLaboratoireIdAndDeletedAtIsNull(UUID laboratoireId);

    List<Paiement> findByLaboratoireIdAndStatutAndDeletedAtIsNull(UUID laboratoireId, StatutPaiementEnum statut);
}
