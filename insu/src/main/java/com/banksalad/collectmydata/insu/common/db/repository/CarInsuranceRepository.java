package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;

import java.util.Optional;

public interface CarInsuranceRepository extends JpaRepository<CarInsuranceEntity, Long> {

  Optional<CarInsuranceEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCarInsuranceNo(long banksaladUserId,
      String organizationId, String insuNum, int carInsuranceNo);
}
