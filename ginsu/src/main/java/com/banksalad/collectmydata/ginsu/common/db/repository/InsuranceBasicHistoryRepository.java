package com.banksalad.collectmydata.ginsu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicHistoryEntity;

public interface InsuranceBasicHistoryRepository extends JpaRepository<InsuranceBasicHistoryEntity, Long> {
  
}
