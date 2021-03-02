package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;

public interface LoanBasicRepository extends JpaRepository<LoanBasicEntity, Long> {

}
