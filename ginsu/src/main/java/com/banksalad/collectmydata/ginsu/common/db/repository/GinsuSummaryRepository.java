package com.banksalad.collectmydata.ginsu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.ginsu.common.db.entity.GinsuSummaryEntity;

import java.util.List;
import java.util.Optional;

public interface GinsuSummaryRepository extends JpaRepository<GinsuSummaryEntity, Long> {

  Optional<GinsuSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(
      long banaksaladUserId,
      String organizationId,
      String insuNum
  );

  List<GinsuSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(
      long banksaladUserId,
      String organizationId
  );
}
