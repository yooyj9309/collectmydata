package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicHistoryEntity;

public interface DepositAccountBasicHistoryRepository extends JpaRepository<DepositAccountBasicHistoryEntity, Long> {

}
