package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionEntity;

import java.util.Optional;

public interface LoanAccountTransactionRepository extends JpaRepository<LoanAccountTransactionEntity, Long> {

  Optional<LoanAccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndCurrencyCodeAndSeqnoAndUniqueTransNoAndTransactionYearMonth(
      Long banksaladUserId,
      String organizationId,
      String accountNum,
      String currencyCode,
      String seqno,
      String uniqueTransNo,
      Integer transactionYearMonth
  );
}
