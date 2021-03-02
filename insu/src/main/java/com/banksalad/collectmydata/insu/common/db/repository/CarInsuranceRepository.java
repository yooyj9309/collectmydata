package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;

public interface CarInsuranceRepository extends JpaRepository<CarInsuranceEntity, Long> {

}
