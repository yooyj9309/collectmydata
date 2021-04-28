package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestIrpAccountSummaryEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface InvestIrpAccountSummaryRepository extends JpaRepository<InvestIrpAccountSummaryEntity, Long> {

  List<InvestIrpAccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndUpdatedAtGreaterThan(
      long banksaladUserId,
      String organizationId, LocalDateTime updatedAt);

}
