package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountEntity;

public interface DepositAccountRepository extends JpaRepository<DepositAccountEntity, Long> {

  DepositAccountEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
      Long banksaladUserId, String organizationId, String accountNum, Integer seqno, String currencyCode);
}
