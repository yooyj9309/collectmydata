package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.IrpAccountSummaryEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface IrpAccountSummaryRepository extends JpaRepository<IrpAccountSummaryEntity, Long> {

  List<IrpAccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndUpdatedAtGreaterThan(long banksaladUserId,
      String organizationId, LocalDateTime updatedAt);

}
