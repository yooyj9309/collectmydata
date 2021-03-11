package com.banksalad.collectmydata.irp.common.db.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;

import java.util.List;
import java.util.Optional;

public interface IrpAccountBasicRepository extends JpaRepository<IrpAccountBasicEntity, Long> {

  Optional<IrpAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId, String organizationId, String accountNum, String seqno);

  List<IrpAccountBasicEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
