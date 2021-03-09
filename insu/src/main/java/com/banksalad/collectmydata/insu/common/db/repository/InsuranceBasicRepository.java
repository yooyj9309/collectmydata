package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;

import java.util.Optional;

public interface InsuranceBasicRepository extends JpaRepository<InsuranceBasicEntity, Long> {

  Optional<InsuranceBasicEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(Long banksaladUserId,
      String organizationId, String insuNum);
}
