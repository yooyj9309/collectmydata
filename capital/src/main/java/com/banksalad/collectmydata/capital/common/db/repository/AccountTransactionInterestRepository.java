package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;

public interface AccountTransactionInterestRepository extends JpaRepository<AccountTransactionInterestEntity, Long> {

  void deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
      Long banksaladUserId, String organizationId, String accountNum, String seqno, Integer transactionYearMonth,
      String uniqueTransNo);
}
