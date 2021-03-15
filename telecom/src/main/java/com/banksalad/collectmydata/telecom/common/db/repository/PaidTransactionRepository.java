package com.banksalad.collectmydata.telecom.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.telecom.common.db.entity.PaidTransactionEntity;

public interface PaidTransactionRepository extends JpaRepository<PaidTransactionEntity, Long> {

}
