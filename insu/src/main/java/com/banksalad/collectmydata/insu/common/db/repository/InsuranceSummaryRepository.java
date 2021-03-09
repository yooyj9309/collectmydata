package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;

import java.util.Optional;

public interface InsuranceSummaryRepository extends JpaRepository<InsuranceSummaryEntity, Long> {

  Optional<InsuranceSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(Long banksaladUserId,
      String organizationId, String insuNum);
}
