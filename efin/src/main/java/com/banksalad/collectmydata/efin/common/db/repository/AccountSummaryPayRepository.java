package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryPayEntity;

import java.util.List;

public interface AccountSummaryPayRepository extends JpaRepository<AccountSummaryPayEntity, Long> {

  List<AccountSummaryPayEntity> findByBanksaladUserIdAndOrganizationIdAndSubKeyAndAccountId(long bankSaladUserId,
      String organizationId, String subKey, String accountId);

  void deleteAllByBanksaladUserIdAndOrganizationIdAndSubKeyAndAccountId(long banksaladUserId, String organizationId,
      String subKey, String accountId);
}
