package com.banksalad.collectmydata.referencebank.common.service;

import com.banksalad.collectmydata.referencebank.common.dto.UserSyncStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSyncStatusService {

  void updateUserSyncStatus(long banksaladUserId, String organizationId, String apiId, LocalDateTime syncedAt);

  UserSyncStatus getUserSyncStatus(long banksaladUserId, String organizationId, String apiId);

  List<UserSyncStatus> getUserSyncStatus(long banksaladUserId, String organizationId);
}
