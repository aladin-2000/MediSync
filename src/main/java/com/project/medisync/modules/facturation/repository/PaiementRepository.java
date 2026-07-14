package com.project.medisync.modules.facturation.repository;

import com.project.medisync.modules.facturation.entity.Paiement;
import com.project.medisync.modules.facturation.entity.StatutPaiementEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, String> {

    Optional<Paiement> findByIdAndDeletedAtIsNull(String id);

    List<Paiement> findByLaboratoireIdAndDeletedAtIsNull(String laboratoireId);

    List<Paiement> findByLaboratoireIdAndStatutAndDeletedAtIsNull(String laboratoireId, StatutPaiementEnum statut);
}
