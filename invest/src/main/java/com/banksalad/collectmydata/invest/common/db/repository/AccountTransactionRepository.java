package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.AccountTransactionEntity;

import java.util.Optional;

public interface AccountTransactionRepository extends JpaRepository<AccountTransactionEntity, Long> {

  Optional<AccountTransactionEntity> findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNo(
      Integer transactionYearMonth, Long banksaladUserId, String organizationId, String accountNum,
      String uniqueTransNo);
}
