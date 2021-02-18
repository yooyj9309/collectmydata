package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.UserSyncStatusEntity;

import java.util.Optional;

public interface UserSyncStatusRepository extends JpaRepository<UserSyncStatusEntity, Long> {

  public Optional<UserSyncStatusEntity> findByBanksaladUserIdAndOrganizationIdAndApiIdAndIsDeleted(
      Long banksaladUserId, String organizationId, String apiId, Boolean isDeleted);
}
