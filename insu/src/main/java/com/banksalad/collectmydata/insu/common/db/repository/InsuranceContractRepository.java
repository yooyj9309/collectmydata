package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;

public interface InsuranceContractRepository extends JpaRepository<InsuranceContractEntity, Long> {

}
