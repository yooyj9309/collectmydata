package com.banksalad.collectmydata.referencebank.common.db.repository;


import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountDetailEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepositAccountDetailRepository extends JpaRepository<DepositAccountDetailEntity, Long> {

  Optional<DepositAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
      Long banksaladUserId, String organizationId, String accountNum, String seqno, String currencyCode);

  List<DepositAccountDetailEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
