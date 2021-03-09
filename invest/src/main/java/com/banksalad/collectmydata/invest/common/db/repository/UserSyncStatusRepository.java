package com.banksalad.collectmydata.invest.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.invest.common.db.entity.UserSyncStatusEntity;

import java.util.Optional;

public interface UserSyncStatusRepository extends JpaRepository<UserSyncStatusEntity, Long> {

  Optional<UserSyncStatusEntity> findByBanksaladUserIdAndOrganizationIdAndApiId(Long banksaladUserId,
      String organizationId, String apiId);
}
