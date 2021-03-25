package com.banksalad.collectmydata.ginsu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicEntity;

import java.util.Optional;

public interface InsuranceBasicRepository extends JpaRepository<InsuranceBasicEntity, Long> {

  Optional<InsuranceBasicEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(
      long banaksaladUserId,
      String organizationId,
      String insuNum
  );
}
