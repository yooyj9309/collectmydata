package com.banksalad.collectmydata.efin.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.efin.common.db.entity.ChargeHistoryEntity;

public interface ChargeHistoryRepository extends JpaRepository<ChargeHistoryEntity, Long> {

}
