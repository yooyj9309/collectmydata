package com.banksalad.collectmydata.ginsu.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserSyncStatus {

  private final LocalDateTime syncedAt;
  private final long searchTimestamp;
  private final long banksaladUserId;
  private final String organizationId;
  private final String apiId;
}
