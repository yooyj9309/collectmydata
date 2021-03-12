package com.banksalad.collectmydata.irp.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.irp.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.irp.common.db.repository.UserSyncStatusRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncStatusServiceImpl implements UserSyncStatusService {

  private static final long DEFAULT_SEARCH_TIMESTAMP = 0L;
  private final UserSyncStatusRepository userSyncStatusRepository;

  @Transactional
  @Override
  public void updateUserSyncStatus(long banksaladUserId, String organizationId, String apiId, LocalDateTime syncedAt,
      Long searchTimestamp, boolean isAllResponseResultOk) {

    UserSyncStatusEntity userSyncStatusEntity = getUserSyncStatus(banksaladUserId, organizationId, apiId, syncedAt,
        isAllResponseResultOk);

    if(userSyncStatusEntity != null) {
      userSyncStatusEntity.setSearchTimestamp(searchTimestamp);
      userSyncStatusRepository.save(userSyncStatusEntity);
    }
  }

  @Transactional
  @Override
  public void updateUserSyncStatus(long banksaladUserId, String organizationId, String apiId, LocalDateTime syncedAt,
      boolean isAllResponseResultOk) {

    UserSyncStatusEntity userSyncStatusEntity = getUserSyncStatus(banksaladUserId, organizationId, apiId, syncedAt,
        isAllResponseResultOk);

    if(userSyncStatusEntity != null) {
      userSyncStatusRepository.save(userSyncStatusEntity);
    }
  }

  @Override
  public long getSearchTimestamp(long banksaladUserId, String organizationId, String apiId) {

    return userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndApiId(
        banksaladUserId, organizationId, apiId
    ).map(UserSyncStatusEntity::getSearchTimestamp).orElse(DEFAULT_SEARCH_TIMESTAMP);
  }

  private UserSyncStatusEntity getUserSyncStatus(long banksaladUserId, String organizationId, String apiId, LocalDateTime syncedAt,
      boolean isAllResponseResultOk) {

    UserSyncStatusEntity userSyncStatusEntity = null;
    if (isAllResponseResultOk) {

      userSyncStatusEntity = userSyncStatusRepository
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
    }

    return userSyncStatusEntity;
  }
}
