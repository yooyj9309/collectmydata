package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface AccountSummaryRepository extends JpaRepository<AccountSummaryEntity, Long> {

  Optional<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, String seqno);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndIsConsentIsTrueAndAccountType(Long banksaladUserId,
      String organizationId, String accountType);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndIsConsentIsTrueAndAccountTypeNot(Long banksaladUserId,
      String organizationId, String accountType);
}
