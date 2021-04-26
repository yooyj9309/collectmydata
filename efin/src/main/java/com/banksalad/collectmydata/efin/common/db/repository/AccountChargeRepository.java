package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.AccountChargeEntity;

import java.util.Optional;

public interface AccountChargeRepository extends JpaRepository<AccountChargeEntity, Long> {

  Optional<AccountChargeEntity> findByBanksaladUserIdAndOrganizationIdAndSubKey(long banksaladUserId, String organizationId,
      String subKey);
}
