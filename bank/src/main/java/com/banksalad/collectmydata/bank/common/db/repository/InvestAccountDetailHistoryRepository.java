package com.banksalad.collectmydata.bank.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailHistoryEntity;

public interface InvestAccountDetailHistoryRepository extends JpaRepository<InvestAccountDetailHistoryEntity, Long> {

}
