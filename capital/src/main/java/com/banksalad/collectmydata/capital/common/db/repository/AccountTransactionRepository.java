package com.banksalad.collectmydata.capital.common.db.repository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountTransactionRepository extends JpaRepository<AccountTransactionEntity, Long> {

  List<AccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoNotIn(
      long banksaladUserId, String organizationId, String accountNum, Integer seqno,
      Integer transactionYearMonth, List<String> uniqueTransNo);

  List<AccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoIn(
      long banksaladUserId, String organizationId, String accountNum, Integer seqno,
      Integer transactionYearMonth, List<String> uniqueTransNo);
}
