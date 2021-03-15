package com.banksalad.collectmydata.telecom.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;

public interface TelecomSummaryRepository extends JpaRepository<TelecomSummaryEntity, Long> {

}
