package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.AccountPrepaidTransactionEntity;

import java.util.Optional;

public interface AccountPrepaidTransactionRepository extends JpaRepository<AccountPrepaidTransactionEntity, Long> {

  Optional<AccountPrepaidTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndTransactionYearMonthAndSubKeyAndFobNameAndUniqueTransNo(
      Long banksaladUserId, String organizationId, Integer transactionYearMonth, String subKey, String fobName,
      String uniqueTransNo);
}
