package com.banksalad.collectmydata.irp.service;

import com.banksalad.collectmydata.common.collect.api.Api;

import java.time.LocalDateTime;

public interface UserSyncStatusService {

  void updateUserSyncStatus(long banksaladUserId, String organizationId, String apiId,
      LocalDateTime syncedAt, Long searchTimestamp, boolean isAllResponseResultOk);

  long getSearchTimestamp(long banksaladUserId, String organizationId, Api api);
}
