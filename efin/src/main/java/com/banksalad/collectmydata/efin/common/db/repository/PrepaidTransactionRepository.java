package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.PrepaidTransactionEntity;

import java.util.Optional;

public interface PrepaidTransactionRepository extends JpaRepository<PrepaidTransactionEntity, Long> {

  Optional<PrepaidTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndTransactionYearMonthAndSubKeyAndFobNameAndUniqueTransNo(
      Long banksaladUserId, String organizationId, Integer transactionYearMonth, String subKey, String fobName,
      String uniqueTransNo);
}
