package com.banksalad.collectmydata.capital.common.db.repository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountTransactionInterestRepository extends JpaRepository<AccountTransactionInterestEntity, Long> {

  void deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoIn(
      long banksaladUserId, String organizationId, String accountNum, Integer seqno,
      Integer transactionYearMonth, List<String> uniqueTransNo);
}
