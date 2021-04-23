package com.banksalad.collectmydata.telecom.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.telecom.common.db.entity.TelecomBillHistoryEntity;

public interface TelecomBillHistoryRepository extends JpaRepository<TelecomBillHistoryEntity, Long> {

}
