package com.banksalad.collectmydata.schedule.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;


public interface ScheduledSyncRepository extends JpaRepository<ScheduledSyncEntity, Long> {

  void deleteByBanksaladUserIdAndSectorAndIndustryAndOrganizationIdAndConsentId(
      Long banksaladUserId, String sector, String industry, String organizationId, String consentId
  );
}
