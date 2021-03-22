package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionInterestEntity;

public interface LoanAccountTransactionInterestRepository extends
    JpaRepository<LoanAccountTransactionInterestEntity, Long> {

  void deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNoAndTransactionYearMonth(
      Long banksaladUserId,
      String organizationId,
      String accountNum,
      String uniqueTransNo,
      Integer transactionYearMonth
  );
}
