package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestIrpAccountDetailEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface InvestIrpAccountDetailRepository extends JpaRepository<InvestIrpAccountDetailEntity, Long> {

  int countByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThan(
      long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime updatedAt);

  List<InvestIrpAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThan(
      long banksaladUserId, String organizationId, String accountNum, String seqno, LocalDateTime updatedAt,
      Pageable pageable);
}
