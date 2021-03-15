package com.banksalad.collectmydata.irp.common.db.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;

import java.util.List;

public interface IrpAccountDetailRepository extends JpaRepository<IrpAccountDetailEntity, Long> {

  List<IrpAccountDetailEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);

  void deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId, String organizationId,
      String accountNum, String seqno);

  List<IrpAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId, String organizationId, String accountNum, String seqno);
}
