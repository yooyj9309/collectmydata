package com.banksalad.collectmydata.insu.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.insu.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.insu.common.db.repository.UserSyncStatusRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncStatusServiceImpl implements UserSyncStatusService {

  private final UserSyncStatusRepository userSyncStatusRepository;
  private static final long DEFAULT_SEARCH_TIMESTAMP = 0L;

  @Transactional
  @Override
  public void updateUserSyncStatus(long banksaladUserId, String organizationId, String apiId, LocalDateTime syncedAt,
      Long searchTimestamp, boolean isAllResponseResultOk) {
    if (isAllResponseResultOk) {
      UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
          .findByBanksaladUserIdAndOrganizationIdAndApiId(
              banksaladUserId,
              organizationId,
              apiId
          ).orElseGet(() ->
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
  }

  @Override
  public long getSearchTimestamp(long banksaladUserId, String organizationId, Api api) {
    return userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndApiId(
        banksaladUserId, organizationId, api.getId()
    ).map(UserSyncStatusEntity::getSearchTimestamp).orElse(DEFAULT_SEARCH_TIMESTAMP);
  }
}
