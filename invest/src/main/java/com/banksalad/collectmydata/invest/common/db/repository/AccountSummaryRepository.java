package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface AccountSummaryRepository extends JpaRepository<AccountSummaryEntity, Long> {

  Optional<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long banksaladUserId,
      String organizationId, String accountNum);

  List<AccountSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(Long banksaladUserId,
      String organizationId);
}
