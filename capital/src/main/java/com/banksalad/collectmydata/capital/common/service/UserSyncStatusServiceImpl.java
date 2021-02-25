package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.capital.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.common.collect.api.Api;
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
          .findByBanksaladUserIdAndOrganizationIdAndApiIdAndIsDeleted(
              banksaladUserId,
              organizationId,
              apiId,
              false
          ).orElseGet(() ->
              UserSyncStatusEntity.builder()
                  .banksaladUserId(banksaladUserId)
                  .organizationId(organizationId)
                  .apiId(apiId)
                  .isDeleted(false)
                  .build()
          );

      userSyncStatusEntity.setSyncedAt(syncedAt);
      userSyncStatusEntity.setSearchTimestamp(searchTimestamp);
      userSyncStatusRepository.save(userSyncStatusEntity);
    }
  }

  @Override
  public long getSearchTimestamp(long banksaladUserId, String organizationId, Api api) {
    return userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndApiIdAndIsDeleted(
        banksaladUserId, organizationId, api.getId(), false
    ).map(UserSyncStatusEntity::getSearchTimestamp).orElse(DEFAULT_SEARCH_TIMESTAMP);
  }
}
