package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.BillEntity;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<BillEntity, Long> {

  List<BillEntity> findByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);

  Optional<BillEntity> findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndCardTypeAndSeqno(long banksaladUserId,
      String organizationId, int chargeMonth, String cardType, String seqno);
}
