package com.banksalad.collectmydata.schedule.sync.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduledSyncRequest {

  private final String banksaladUserId;
  private final String sector;
  private final String industry;
  private final String organizationId;
}
