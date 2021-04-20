package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountSummaryRepository extends JpaRepository<AccountSummaryEntity, Long> {

  Optional<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, String seqno);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(Long banksaladUserId,
      String organizationId);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsentIsTrueAndAccountTypeIn(
      Long banksaladUserId, String organizationId, Collection<String> accountTypes);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndBasicResponseCodeNotInAndConsentIsTrue(
      Long banksaladUserId, String organizationId, String[] responseCode);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndDetailResponseCodeNotInAndConsentIsTrue(
      Long banksaladUserId, String organizationId, String[] responseCode);

  Optional<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionResponseCodeNotInAndConsentIsTrue(
      Long banksaladUserId, String organizationId, String accountNum, String seqno, String[] responseCode);
}
