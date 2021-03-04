package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;

import java.util.List;

public interface DepositAccountBasicRepository extends JpaRepository<DepositAccountBasicEntity, Long> {

  DepositAccountBasicEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
      Long banksaladUserId, String organizationId, String accountNum, String seqno, String currencyCode);

  List<DepositAccountBasicEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
