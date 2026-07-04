package com.project.medisync.modules.facturation.service;

import com.project.medisync.modules.facturation.entity.MethodePaiementEnum;
import com.project.medisync.modules.facturation.entity.Paiement;
import com.project.medisync.modules.facturation.entity.StatutPaiementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Interface publique du module Facturation.
 * Utilisée notamment par le module Profils pour lier un paiement à un laboratoire.
 */
public interface PaiementService {

    Paiement create(UUID laboratoireId, BigDecimal montant, String devise,
                    StatutPaiementEnum statut, MethodePaiementEnum methode,
                    String referenceExterne, LocalDate periodeDebut, LocalDate periodeFin);

    Paiement getById(UUID id);

    List<Paiement> getByLaboratoire(UUID laboratoireId);

    Paiement updateStatut(UUID id, StatutPaiementEnum nouveauStatut);

    void delete(UUID id);
}
