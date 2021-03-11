package com.banksalad.collectmydata.irp.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.irp.common.db.entity.ApiLogEntity;

public interface ApiLogRepository extends JpaRepository<ApiLogEntity, Long> {

}
