package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;

public interface DepositAccountTransactionRepository extends JpaRepository<DepositAccountTransactionEntity, Long> {

  DepositAccountTransactionEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCodeAndUniqueTransNoAndTransactionYearMonth(
      Long banksaladUserId, String organizationId, String accountNum, Integer seqno, String currencyCode,
      String uniqueTransNo, String transactionYearMonth);
}
