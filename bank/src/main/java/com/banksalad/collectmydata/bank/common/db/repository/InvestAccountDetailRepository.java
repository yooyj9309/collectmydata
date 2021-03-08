package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;

import java.util.List;

public interface InvestAccountDetailRepository extends JpaRepository<InvestAccountDetailEntity, Long> {

  InvestAccountDetailEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
      Long banksaladUserId,
      String organizationId,
      String accountNum,
      String seqno,
      String currencyCode);

  List<InvestAccountDetailEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
