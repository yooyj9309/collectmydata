package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.LoanSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface LoanSummaryRepository extends JpaRepository<LoanSummaryEntity, Long> {

  Optional<LoanSummaryEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);

  List<LoanSummaryEntity> findAllByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
