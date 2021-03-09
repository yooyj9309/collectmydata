package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailHistoryEntity;

public interface AccountDetailHistoryRepository extends JpaRepository<AccountDetailHistoryEntity, Long> {

}
