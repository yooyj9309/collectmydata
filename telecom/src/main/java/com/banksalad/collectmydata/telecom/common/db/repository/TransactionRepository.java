package com.banksalad.collectmydata.telecom.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.telecom.common.db.entity.TransactionEntity;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

  Optional<TransactionEntity> findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransMonth(Long banksaladUserId,
      String organizationId, String mgmtId, Integer transMonth);
}
