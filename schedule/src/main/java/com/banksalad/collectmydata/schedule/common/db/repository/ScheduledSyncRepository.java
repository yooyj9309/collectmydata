package com.banksalad.collectmydata.schedule.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;

public interface ScheduledSyncRepository extends JpaRepository<ScheduledSync, Long> {

}
