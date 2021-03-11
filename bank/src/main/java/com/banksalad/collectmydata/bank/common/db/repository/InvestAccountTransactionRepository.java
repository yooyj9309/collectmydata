package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;

public interface InvestAccountTransactionRepository extends JpaRepository<InvestAccountTransactionEntity, Long> {

  InvestAccountTransactionEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNoAndTransactionYearMonth(
      Long banksaladUserId,
      String organizationId,
      String accountNum,
      String seqno,
      String uniqueTransNo,
      Integer transactionYearMonth);
}
