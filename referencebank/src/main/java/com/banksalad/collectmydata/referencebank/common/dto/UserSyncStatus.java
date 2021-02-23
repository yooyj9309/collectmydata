package com.banksalad.collectmydata.referencebank.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserSyncStatus {

  private final long banksaladUserId;
  private final String organizationId;
  private final LocalDateTime syncedAt;
}
