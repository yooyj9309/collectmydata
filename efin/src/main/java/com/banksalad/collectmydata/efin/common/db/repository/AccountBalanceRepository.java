package com.banksalad.collectmydata.efin.common.db.repository;

import com.banksalad.collectmydata.efin.common.db.entity.AccountBalanceEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountBalanceRepository extends JpaRepository<AccountBalanceEntity, Long> {

  Optional<AccountBalanceEntity> findByBanksaladUserIdAndOrganizationIdAndSubKeyAndFobName(long banksaladUserId,
      String organizationId, String subKey, String fobName);
}
