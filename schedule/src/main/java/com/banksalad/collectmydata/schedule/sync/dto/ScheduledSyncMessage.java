package com.banksalad.collectmydata.schedule.sync.dto;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.enums.SyncType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduledSyncMessage {

  private final String banksaladUserId;

  private final String sector;

  private final String industry;

  private final String organizationId;

  private final SyncType syncType;

  public static ScheduledSyncMessage of(ScheduledSync ScheduledSync, SyncType syncType) {
    return ScheduledSyncMessage.builder()
        .banksaladUserId(ScheduledSync.getBanksaladUserId())
        .sector(ScheduledSync.getSector())
        .industry(ScheduledSync.getIndustry())
        .organizationId(ScheduledSync.getOrganizationId())
        .syncType(syncType)
        .build();
  }
}
