package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;

import java.util.List;
import java.util.Optional;

public interface CarInsuranceRepository extends JpaRepository<CarInsuranceEntity, Long> {

  Optional<CarInsuranceEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCarNumber(Long banksaladUserId,
      String organizationId, String insuNum, String carNumber);

  List<CarInsuranceEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(Long banksaladUserId, String organizationId,
      String insuNum);

  List<CarInsuranceEntity> findByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
