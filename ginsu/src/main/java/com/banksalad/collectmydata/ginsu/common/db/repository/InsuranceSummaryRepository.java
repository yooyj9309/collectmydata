package com.banksalad.collectmydata.ginsu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface InsuranceSummaryRepository extends JpaRepository<InsuranceSummaryEntity, Long> {

  Optional<InsuranceSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(
      long banaksaladUserId,
      String organizationId,
      String insuNum
  );

  List<InsuranceSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(
      long banksaladUserId,
      String organizationId
  );
}
