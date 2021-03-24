package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceTransactionEntity;

import java.util.Optional;

public interface CarInsuranceTransactionRepository extends JpaRepository<CarInsuranceTransactionEntity, Long> {

  Optional<CarInsuranceTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCarNumberAndTransNo(
      Long banksaladUserId, String organizationId, String insuNum, String carNumber, int transNo);
}
