package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountTransactionEntity;

public interface BankIrpAccountTransactionRepository extends JpaRepository<BankIrpAccountTransactionEntity, Long> {

}
