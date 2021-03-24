package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.BalanceHistoryEntity;

public interface BalanceHistoryRepository extends JpaRepository<BalanceHistoryEntity, Long> {

}
