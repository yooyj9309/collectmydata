package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountDetailEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface BankIrpAccountDetailRepository extends JpaRepository<BankIrpAccountDetailEntity, Long> {

  int countByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThan(
      long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime updatedAt);

  List<BankIrpAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThan(
      long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime updatedAt,
      Pageable pageable);
}
