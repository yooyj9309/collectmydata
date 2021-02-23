package com.banksalad.collectmydata.referencebank.common.service;

import com.banksalad.collectmydata.referencebank.common.dto.UserSyncStatus;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSyncStatusServiceImpl implements UserSyncStatusService {

  @Override
  public void updateUserSyncStatus(long banksaladUserId, String organizationId, String apiId, LocalDateTime syncedAt) {
    // TODO : implement
  }

  @Override
  public UserSyncStatus getUserSyncStatus(long banksaladUserId, String organizationId, String apiId) {
    // TODO : implement
    return UserSyncStatus.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .build();
  }

  @Override
  public List<UserSyncStatus> getUserSyncStatus(long banksaladUserId, String organizationId) {
    // TODO : implement
    return List.of(UserSyncStatus.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId("organizationId")
        .build());
  }
}
