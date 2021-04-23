package com.banksalad.collectmydata.telecom.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.telecom.common.db.entity.TelecomBillEntity;

import java.util.Optional;

public interface TelecomBillRepository extends JpaRepository<TelecomBillEntity, Long> {

  Optional<TelecomBillEntity> findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndMgmtId(Long banksaladUserId,
      String organizationId, int chargeMonth, String mgmtId);
}
