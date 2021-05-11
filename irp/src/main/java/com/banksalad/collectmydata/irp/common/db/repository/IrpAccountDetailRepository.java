package com.banksalad.collectmydata.irp.common.db.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;

import java.util.List;

public interface IrpAccountDetailRepository extends JpaRepository<IrpAccountDetailEntity, Long> {

  List<IrpAccountDetailEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from IrpAccountDetailEntity where banksaladUserId = :banksaladUserId and organizationId = :organizationId and accountNum = :accountNum and seqno = :seqno")
  void deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(@Param("banksaladUserId") Long banksaladUserId,
      @Param("organizationId") String organizationId,
      @Param("accountNum") String accountNum, @Param("seqno") String seqno);

  List<IrpAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoOrderByIrpDetailNoDesc(
      Long banksaladUserId,
      String organizationId, String accountNum, String seqno);
}
