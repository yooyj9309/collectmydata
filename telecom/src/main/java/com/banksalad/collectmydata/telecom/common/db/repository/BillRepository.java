package com.banksalad.collectmydata.telecom.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.telecom.common.db.entity.BillEntity;

import java.util.Optional;

public interface BillRepository extends JpaRepository<BillEntity, Long> {

  Optional<BillEntity> findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndMgmtId(Long banksaladUserId,
      String organizationId, int chargeMonth, String mgmtId);
}
