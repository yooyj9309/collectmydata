package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionInterestEntity;

public interface LoanTransactionInterestRepository extends JpaRepository<LoanTransactionInterestEntity, Long> {

  @Transactional
  void deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
      long banksaladUserId, String organizationId, String accountNum, String transDtime, String transNo,
      Integer transactionYearMonth
  );
}
