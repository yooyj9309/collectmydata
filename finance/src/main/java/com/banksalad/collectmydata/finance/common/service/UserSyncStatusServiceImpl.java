package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserSyncStatusServiceImpl implements UserSyncStatusService {

  private static final long DEFAULT_SEARCH_TIMESTAMP = 0L;
  private final UserSyncStatusRepository userSyncStatusRepository;

  public UserSyncStatusServiceImpl(UserSyncStatusRepository userSyncStatusRepository) {
    this.userSyncStatusRepository = userSyncStatusRepository;
  }

  @Override
  public void updateUserSyncStatus(long banksaladUserId, String organizationId, String apiId, LocalDateTime syncedAt,
      long searchTimestamp) {

    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(banksaladUserId, organizationId, apiId)
        .orElseGet(() ->
            UserSyncStatusEntity.builder()
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .apiId(apiId)
                .build()
        );

    userSyncStatusEntity.setSyncedAt(syncedAt);
    userSyncStatusEntity.setSearchTimestamp(searchTimestamp);

    userSyncStatusRepository.save(userSyncStatusEntity);
  }

  @Override
  public long getSearchTimestamp(long banksaladUserId, String organizationId, Api api) {
    return
        userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndApiId(banksaladUserId, organizationId, api.getId())
            .map(UserSyncStatusEntity::getSearchTimestamp)
            .orElse(DEFAULT_SEARCH_TIMESTAMP);
  }
}
