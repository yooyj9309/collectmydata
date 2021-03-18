package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;

import java.util.List;
import java.util.Optional;

public interface DepositAccountDetailRepository extends JpaRepository<DepositAccountDetailEntity, Long> {

  Optional<DepositAccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
      Long banksaladUserId, String organizationId, String accountNum, String seqno, String currencyCode);

  List<DepositAccountDetailEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
