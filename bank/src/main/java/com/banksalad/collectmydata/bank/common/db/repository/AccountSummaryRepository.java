package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;

import java.util.List;

public interface AccountSummaryRepository extends JpaRepository<AccountSummaryEntity, Long> {

  AccountSummaryEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndForeignDeposit(
      long banksaladUserId, String organizationId, String accountNum, String seqno, boolean foreignDeposit);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsent(long banksaladUserId,
      String organizationId, boolean consent);

}
