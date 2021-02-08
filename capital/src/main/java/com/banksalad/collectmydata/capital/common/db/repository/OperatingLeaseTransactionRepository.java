package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseTransactionEntity;

public interface OperatingLeaseTransactionRepository extends JpaRepository<OperatingLeaseTransactionEntity, Long> {

}
