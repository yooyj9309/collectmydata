package com.banksalad.collectmydata.telecom.common.db.repository;

import com.banksalad.collectmydata.telecom.common.db.entity.PaidTransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaidTransactionRepository extends JpaRepository<PaidTransactionEntity, Long> {

  Optional<PaidTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransactionYearMonth(
      Long banksaladUserId, String organizationId, String mgmtId, Integer transactionYearMonth);

  void deleteByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransactionYearMonthAndTransDate(
      Long banksaladUserId, String organizationId, String mgmtId, Integer transactionYearMonth, String transDate);
}
