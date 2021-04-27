package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.AccountTransactionEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AccountTransactionRepository extends JpaRepository<AccountTransactionEntity, Long> {

  Optional<AccountTransactionEntity> findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNo(
      Integer transactionYearMonth, Long banksaladUserId, String organizationId, String accountNum,
      String uniqueTransNo);

  Page<AccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndCreatedAtAfter(
      Long banksaladUserId, String organizationId, String accountNum, LocalDateTime createdAt, Pageable pageable);
}
