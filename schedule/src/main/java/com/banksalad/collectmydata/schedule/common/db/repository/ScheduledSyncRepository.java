package com.banksalad.collectmydata.schedule.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;

import java.util.List;
import java.util.Optional;

// TODO : How to balance load
public interface ScheduledSyncRepository extends JpaRepository<ScheduledSync, Long> {

  List<ScheduledSync> findAllByIsDeletedEquals(Boolean isDeleted);

  Optional<ScheduledSync> findByBanksaladUserIdAndSectorAndIndustryAndOrganizationIdAndIsDeleted(
      String banksaladUserId, String sector, String industry, String organizationId, Boolean isDeleted);
}
