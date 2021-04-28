package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestIrpAccountBasicEntity;

import java.util.Optional;

public interface InvestIrpAccountBasicRepository extends JpaRepository<InvestIrpAccountBasicEntity, Long> {

  Optional<InvestIrpAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
