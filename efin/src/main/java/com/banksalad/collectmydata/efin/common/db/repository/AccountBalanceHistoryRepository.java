package com.banksalad.collectmydata.efin.common.db.repository;

import com.banksalad.collectmydata.efin.common.db.entity.AccountBalanceHistoryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountBalanceHistoryRepository extends JpaRepository<AccountBalanceHistoryEntity, Long> {

}
