package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;

import java.util.List;
import java.util.Optional;

public interface InvestAccountDetailRepository extends JpaRepository<InvestAccountDetailEntity, Long> {

  Optional<InvestAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
      Long banksaladUserId,
      String organizationId,
      String accountNum,
      String seqno,
      String currencyCode);

  List<InvestAccountDetailEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
