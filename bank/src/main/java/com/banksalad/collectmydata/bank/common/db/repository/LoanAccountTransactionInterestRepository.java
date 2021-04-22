package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionInterestEntity;

import java.util.List;

public interface LoanAccountTransactionInterestRepository extends
    JpaRepository<LoanAccountTransactionInterestEntity, Long> {

  void deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNoAndTransactionYearMonth(
      Long banksaladUserId, String organizationId, String accountNum, String uniqueTransNo,
      Integer transactionYearMonth);

  List<LoanAccountTransactionInterestEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNo(
      Long banksaladUserId, String organizationId, String accountNum, String uniqueTransNo);
}
