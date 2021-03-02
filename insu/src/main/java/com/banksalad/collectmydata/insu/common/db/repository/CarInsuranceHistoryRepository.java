package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceHistoryEntity;

public interface CarInsuranceHistoryRepository extends JpaRepository<CarInsuranceHistoryEntity, Long> {

}
