package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.IrpAccountBasicEntity;

import java.util.Optional;

public interface IrpAccountBasicRepository extends JpaRepository<IrpAccountBasicEntity, Long> {

  Optional<IrpAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
