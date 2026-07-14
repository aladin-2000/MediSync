package com.project.medisync.modules.reservations.repository;

import com.project.medisync.modules.reservations.entity.PropositionRemplacement;
import com.project.medisync.modules.reservations.entity.StatutPropositionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropositionRemplacementRepository extends JpaRepository<PropositionRemplacement, String> {

    Optional<PropositionRemplacement> findById(String id);

    List<PropositionRemplacement> findByDelegueIdAndStatut(String delegueId, StatutPropositionEnum statut);

    List<PropositionRemplacement> findByStatut(StatutPropositionEnum statut);
}
