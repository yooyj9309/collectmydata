package com.banksalad.collectmydata.telecom.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.telecom.common.db.entity.TelecomTransactionEntity;

import java.util.Optional;

public interface TelecomTransactionRepository extends JpaRepository<TelecomTransactionEntity, Long> {

  Optional<TelecomTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransMonth(Long banksaladUserId,
      String organizationId, String mgmtId, Integer transMonth);
}
