package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicHistoryEntity;

public interface AccountBasicHistoryRepository extends JpaRepository<AccountBasicHistoryEntity, Long> {

}
