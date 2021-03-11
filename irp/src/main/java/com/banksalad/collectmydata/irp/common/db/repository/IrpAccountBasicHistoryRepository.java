package com.banksalad.collectmydata.irp.common.db.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicHistoryEntity;

public interface IrpAccountBasicHistoryRepository extends JpaRepository<IrpAccountBasicHistoryEntity, Long> {

}
