package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.BalanceEntity;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<BalanceEntity, Long> {

  Optional<BalanceEntity> findByBanksaladUserIdAndOrganizationIdAndSubKeyAndFobName(long banksaladUserId,
      String organizationId, String subKey, String fobName);
}
