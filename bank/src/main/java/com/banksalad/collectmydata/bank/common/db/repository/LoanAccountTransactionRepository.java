package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoanAccountTransactionRepository extends JpaRepository<LoanAccountTransactionEntity, Long> {

  Optional<LoanAccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNoAndTransactionYearMonth(
      Long banksaladUserId, String organizationId, String accountNum, String seqno, String uniqueTransNo,
      Integer transactionYearMonth);

  Page<LoanAccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCreatedAtAfter(
      Long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime createdAt,
      Pageable pageable);
}
