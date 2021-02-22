package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.AccountListEntity;

public interface AccountListRepository extends JpaRepository<AccountListEntity, Long> {

  AccountListEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
      Long banksaladUserId, String organizationId, String accountNum, Integer seqno, String currencyCode);

}
