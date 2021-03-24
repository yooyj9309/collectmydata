package com.banksalad.collectmydata.connect.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.connect.common.db.entity.ApiSyncStatusEntity;

import java.util.Optional;

public interface ApiSyncStatusRepository extends JpaRepository<ApiSyncStatusEntity, Long> {

  Optional<ApiSyncStatusEntity> findByApiId(String apiId);
}
