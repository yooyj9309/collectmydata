package com.banksalad.collectmydata.capital.common.db.repository;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseBasicEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatingLeaseBasicRepository extends JpaRepository<OperatingLeaseBasicEntity, Long> {

  Optional<OperatingLeaseBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
