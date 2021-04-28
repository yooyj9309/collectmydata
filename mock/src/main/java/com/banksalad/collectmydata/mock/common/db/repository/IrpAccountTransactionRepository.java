package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.IrpAccountTransactionEntity;

public interface IrpAccountTransactionRepository extends JpaRepository<IrpAccountTransactionEntity, Long> {

}
