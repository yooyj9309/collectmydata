package com.banksalad.collectmydata.capital.common.db.repository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountTransactionRepository extends JpaRepository<AccountTransactionEntity, Long> {

  List<AccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoNotIn(
      long banksaladUserId, String organizationId, String accountNum, String seqno, Integer transactionYearMonth,
      List<String> uniqueTransNo);

  List<AccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoIn(
      long banksaladUserId, String organizationId, String accountNum, String seqno, Integer transactionYearMonth,
      List<String> uniqueTransNo);

  Optional<AccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
      long banksaladUserId, String organizationId, String accountNum, String seqno, Integer transactionYearMonth,
      String uniqueTransNo);
}
