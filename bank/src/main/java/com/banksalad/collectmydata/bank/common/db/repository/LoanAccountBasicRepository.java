package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;

public interface LoanAccountBasicRepository extends JpaRepository<LoanAccountBasicEntity, Long> {

}
