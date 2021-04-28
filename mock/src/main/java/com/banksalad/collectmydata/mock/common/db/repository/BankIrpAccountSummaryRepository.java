package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountSummaryEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface BankIrpAccountSummaryRepository extends JpaRepository<BankIrpAccountSummaryEntity, Long> {

  List<BankIrpAccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndUpdatedAtGreaterThan(long banksaladUserId,
      String organizationId, LocalDateTime updatedAt);

}
