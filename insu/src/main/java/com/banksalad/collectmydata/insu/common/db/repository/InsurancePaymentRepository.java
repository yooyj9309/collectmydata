package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;

import java.util.Optional;

public interface InsurancePaymentRepository extends JpaRepository<InsurancePaymentEntity, Long> {

  Optional<InsurancePaymentEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(
      Long banksaladUserId, String organizationId, String insuNum
  );
}
