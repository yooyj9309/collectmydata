package com.banksalad.collectmydata.telecom.common.db.repository;

import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelecomSummaryRepository extends JpaRepository<TelecomSummaryEntity, Long> {

  Optional<TelecomSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndMgmtId(Long banksaladUserId,
      String organizationId, String mgmtId);
}
