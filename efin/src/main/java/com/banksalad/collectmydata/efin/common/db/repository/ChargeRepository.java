package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.ChargeEntity;

import java.util.Optional;

public interface ChargeRepository extends JpaRepository<ChargeEntity, Long> {

  Optional<ChargeEntity> findByBanksaladUserIdAndOrganizationIdAndSubKey(long banksaladUserId, String organizationId,
      String subKey);
}
