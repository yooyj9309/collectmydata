package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.capital.common.db.repository.UserSyncStatusRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncStatusServiceImpl implements UserSyncStatusService {

  private final UserSyncStatusRepository userSyncStatusRepository;

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
}
