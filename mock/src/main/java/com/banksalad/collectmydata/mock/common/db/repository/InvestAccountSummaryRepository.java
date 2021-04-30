package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountSummaryEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface InvestAccountSummaryRepository extends JpaRepository<InvestAccountSummaryEntity, Long> {

  List<InvestAccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndUpdatedAtGreaterThan(long banksaladUserId,
      String organizationId, LocalDateTime updatedAt);

}
