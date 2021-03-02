package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceTransactionEntity;

public interface CarInsuranceTransactionRepository extends JpaRepository<CarInsuranceTransactionEntity, Long> {

}
