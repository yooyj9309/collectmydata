package com.banksalad.collectmydata.capital.common.service;

import java.time.LocalDateTime;

public interface UserSyncStatusService {

  void updateUserSyncStatus(long banksaladUserId, String organizationId, String apiId,
      LocalDateTime syncedAt, Long searchTimestamp, boolean isAllResponseResultOk);
}
