package com.banksalad.collectmydata.irp.common.db.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountTransactionEntity;

public interface IrpAccountTransactionRepository extends JpaRepository<IrpAccountTransactionEntity, Long> {

}
