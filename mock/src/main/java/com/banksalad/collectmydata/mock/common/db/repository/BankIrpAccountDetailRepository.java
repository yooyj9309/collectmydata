package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.BankIrpAccountDetailEntity;

public interface BankIrpAccountDetailRepository extends JpaRepository<BankIrpAccountDetailEntity, Long> {

}
