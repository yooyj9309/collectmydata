package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoanTransactionRepository extends JpaRepository<LoanTransactionEntity, Long> {

  Optional<LoanTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
      Long banksaladUserId, String organizationId, String accountNum, String transDtime, String transNo,
      Integer transactionYearMonth);

  Page<LoanTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndCreatedAtAfter(long banksaladUserId,
      String organizationId, String accountNum, LocalDateTime createdAt, PageRequest pageRequest);
}
