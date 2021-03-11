package com.banksalad.collectmydata.irp.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface IrpAccountSummaryRepository extends JpaRepository<IrpAccountSummaryEntity, Long> {

  Optional<IrpAccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      long banksaladUserId, String organizationId, String accountNum, String seqno);

  List<IrpAccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndIsConsent(long banksaladUserId,
      String organizationId, boolean isConsent);
}
