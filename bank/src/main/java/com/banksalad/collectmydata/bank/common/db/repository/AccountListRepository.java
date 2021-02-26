package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.AccountListEntity;

import java.util.List;

public interface AccountListRepository extends JpaRepository<AccountListEntity, Long> {

  AccountListEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndIsForeignDeposit(
      long banksaladUserId, String organizationId, String accountNum, String seqno, boolean isForeignDeposit);

  List<AccountListEntity> findByBanksaladUserIdAndOrganizationIdAndIsConsent(long banksaladUserId,
      String organizationId, boolean isConsent);

}
