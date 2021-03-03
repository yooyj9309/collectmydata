package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;

import java.util.List;

public interface AccountSummaryRepository extends JpaRepository<AccountSummaryEntity, Long> {

  AccountSummaryEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndIsForeignDeposit(
      long banksaladUserId, String organizationId, String accountNum, String seqno, boolean isForeignDeposit);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndIsConsent(long banksaladUserId,
      String organizationId, boolean isConsent);

}
