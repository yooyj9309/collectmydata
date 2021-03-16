package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountSummaryRepository extends JpaRepository<AccountSummaryEntity, Long> {

  Optional<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(long banksaladUserId,
      String organizationId, String accountNum, String seqno);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsent(long banksaladUserId,
      String organizationId, boolean consent);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsentIsTrueAndAccountTypeIn(
      long banksaladUserId, String organizationId, Collection<String> accountTypes);

}
