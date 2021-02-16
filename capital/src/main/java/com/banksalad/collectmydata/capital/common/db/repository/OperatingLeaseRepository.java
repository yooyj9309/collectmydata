package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;

import java.util.Optional;

public interface OperatingLeaseRepository extends JpaRepository<OperatingLeaseEntity, Long> {

  Optional<OperatingLeaseEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(long banksaladUserId,
      String organizationId, String accountNum, int seqno);
}
