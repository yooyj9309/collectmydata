package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountBasicEntity;

import java.util.Optional;

public interface BankIrpAccountBasicRepository extends JpaRepository<BankIrpAccountBasicEntity, Long> {

  Optional<BankIrpAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
