package com.banksalad.collectmydata.schedule.sync.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SyncRequest {

  private final String requestId;
  private final String banksaladUserId;
  private final String organizationId;
  private final String orgCode;
}
