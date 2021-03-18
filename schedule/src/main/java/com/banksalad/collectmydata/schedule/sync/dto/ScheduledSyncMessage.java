package com.banksalad.collectmydata.schedule.sync.dto;

import com.banksalad.collectmydata.schedule.common.enums.SyncType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduledSyncMessage {

  private final Long banksaladUserId;

  private final String sector;

  private final String industry;

  private final String organizationId;

  private final SyncType syncType;
}
