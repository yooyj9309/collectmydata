package com.banksalad.collectmydata.schedule.sync.dto;

import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.RegisterScheduledSyncRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncRequest;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduledSyncRequest {

  private final String banksaladUserId;
  private final String sector;
  private final String industry;
  private final String organizationId;

  public static ScheduledSyncRequest of(RegisterScheduledSyncRequest request) {
    return ScheduledSyncRequest.builder()
        .banksaladUserId(request.getBanksaladUserId())
        .sector(request.getSector())
        .industry(request.getIndustry())
        .organizationId(request.getOrganizationId())
        .build();
  }

  public static ScheduledSyncRequest of(UnregisterScheduledSyncRequest request) {
    return ScheduledSyncRequest.builder()
        .banksaladUserId(request.getBanksaladUserId())
        .sector(request.getSector())
        .industry(request.getIndustry())
        .organizationId(request.getOrganizationId())
        .build();
  }
}
