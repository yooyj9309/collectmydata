package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface AccountSummaryRepository extends JpaRepository<AccountSummaryEntity, Long> {

  Optional<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndSubKeyAndAccountId(long banksaladUserId,
      String organizationId, String subKey, String accountId);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(long banksaladUserId,
      String organizationId);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndSubKey(long banksaladUserId,
      String organizationId, String subKey);
}
