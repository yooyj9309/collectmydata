package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.UserSyncStatusEntity;

public interface UserSyncStatusRepository extends JpaRepository<UserSyncStatusEntity, Long> {

  public UserSyncStatusEntity findByOrganizationIdAndBanksaladUserIdAndApiId(String organizationId,
      Long banksaladUserId, String apiId);
}
