package com.banksalad.collectmydata.finance.common.db.repository;

import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSyncStatusRepository extends JpaRepository<UserSyncStatusEntity, Long> {

  Optional<UserSyncStatusEntity> findByBanksaladUserIdAndOrganizationIdAndApiId(
      Long banksaladUserId, String organizationId, String apiId);
}
