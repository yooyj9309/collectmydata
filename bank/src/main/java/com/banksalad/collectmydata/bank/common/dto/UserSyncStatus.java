package com.banksalad.collectmydata.bank.common.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSyncStatus {

  private final long banksaladUserId;
  private final String organizationId;
  private final String organizationCompanyType;
  private final long lastCheckAt;
}
