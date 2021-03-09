package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface DepositAccountTransactionRepository extends JpaRepository<DepositAccountTransactionEntity, Long> {

  DepositAccountTransactionEntity findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCodeAndUniqueTransNo(
      Integer transactionYearMonth, Long banksaladUserId, String organizationId, String accountNum, String seqno,
      String currencyCode, String uniqueTransNo);

  List<DepositAccountTransactionEntity> findByTransactionYearMonthInAndBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndSyncedAtAfter(
      List<Integer> transactionYearMonths, Long banksaladUserId, String organizationId, String accountNum, String seqno,
      LocalDateTime syncedAt);

}
