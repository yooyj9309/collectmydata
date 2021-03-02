package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicHistoryEntity;

public interface LoanBasicHistoryRepository extends JpaRepository<LoanBasicHistoryEntity, Long> {

}
