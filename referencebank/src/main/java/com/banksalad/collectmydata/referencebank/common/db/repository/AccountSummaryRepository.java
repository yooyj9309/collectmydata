package com.banksalad.collectmydata.referencebank.common.db.repository;

import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountSummaryRepository extends JpaRepository<AccountSummaryEntity, Long> {

  Optional<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      long banksaladUserId, String organizationId, String accountNum, String seqno);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndIsConsentIsTrueAndAccountTypeIn(
      long banksaladUserId, String organizationId, Collection<String> accountTypes);

}
