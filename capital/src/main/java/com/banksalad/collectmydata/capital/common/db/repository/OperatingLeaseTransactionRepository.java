package com.banksalad.collectmydata.capital.common.db.repository;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseTransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OperatingLeaseTransactionRepository extends JpaRepository<OperatingLeaseTransactionEntity, Long> {

  Optional<OperatingLeaseTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransDtimeAndTransNoAndTransactionYearMonth(
      long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime transDtime,
      String transNo, Integer transactionYearMonth);
}
