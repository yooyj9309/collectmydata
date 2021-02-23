package com.banksalad.collectmydata.referencebank.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserSyncStatusResponse {

  private final List<UserSyncStatus> userSyncStatuses;

}
