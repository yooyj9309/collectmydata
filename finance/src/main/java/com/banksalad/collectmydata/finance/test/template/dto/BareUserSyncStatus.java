package com.banksalad.collectmydata.finance.test.template.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class BareUserSyncStatus {

  private LocalDateTime syncedAt;

  private long searchTimestamp;
}
