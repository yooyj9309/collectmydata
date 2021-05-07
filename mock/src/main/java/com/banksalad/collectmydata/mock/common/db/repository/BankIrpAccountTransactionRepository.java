package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountTransactionEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface BankIrpAccountTransactionRepository extends JpaRepository<BankIrpAccountTransactionEntity, Long> {

  int countByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThanAndTransDtimeBetween(
      long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime updatedAt,
      String fromDate, String toDate);

  List<BankIrpAccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThanAndTransDtimeBetween(
      long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime updatedAt,
      String fromDate, String toDate, Pageable pageable);
}
