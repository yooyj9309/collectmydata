package com.banksalad.collectmydata.schedule.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.schedule.common.db.entity.SyncEntity;

public interface SyncRepository extends JpaRepository<SyncEntity, Long> {

}
