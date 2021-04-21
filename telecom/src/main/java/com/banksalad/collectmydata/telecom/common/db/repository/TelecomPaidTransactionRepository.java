package com.banksalad.collectmydata.telecom.common.db.repository;

import com.banksalad.collectmydata.telecom.common.db.entity.TelecomPaidTransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelecomPaidTransactionRepository extends JpaRepository<TelecomPaidTransactionEntity, Long> {

  Optional<TelecomPaidTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransactionYearMonth(
      Long banksaladUserId, String organizationId, String mgmtId, Integer transactionYearMonth);

  void deleteByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransactionYearMonthAndTransDate(
      Long banksaladUserId, String organizationId, String mgmtId, Integer transactionYearMonth, String transDate);
}
