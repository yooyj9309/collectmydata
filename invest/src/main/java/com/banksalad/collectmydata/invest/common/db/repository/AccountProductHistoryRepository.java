package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.AccountProductHistoryEntity;

public interface AccountProductHistoryRepository extends JpaRepository<AccountProductHistoryEntity, Long> {

}
