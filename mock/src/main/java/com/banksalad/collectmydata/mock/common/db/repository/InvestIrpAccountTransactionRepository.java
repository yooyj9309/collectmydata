package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestIrpAccountTransactionEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface InvestIrpAccountTransactionRepository extends JpaRepository<InvestIrpAccountTransactionEntity, Long> {

  int countByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThanAndCreatedAtBetween(
      long banksaladUserId,
      String organizationId, String accountNum, String seqno, LocalDateTime updatedAt, LocalDateTime fromCreatedAt,
      LocalDateTime toCreatedAt);

  List<InvestIrpAccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThanAndCreatedAtBetween(
      long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime updatedAt,
      LocalDateTime fromCreatedAt, LocalDateTime toCreatedAt, Pageable pageable);
}
