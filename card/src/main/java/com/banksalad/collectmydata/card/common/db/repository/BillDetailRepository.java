package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;

import java.util.List;

public interface BillDetailRepository extends JpaRepository<BillDetailEntity, Long> {

  List<BillDetailEntity> findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndSeqno(
      Long banksaladUserId, String organizationId, Integer chargeMonth, String seqno);

  void deleteByBanksaladUserIdAndOrganizationIdAndChargeMonthAndSeqno(long banksaladUserId,
      String organizationId, int chargeMonth, String seqno);
}
