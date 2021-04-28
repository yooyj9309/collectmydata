package com.banksalad.collectmydata.mock.irp.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IrpAccountSummarySearch {

  private long banksaladUserId;
  private String organizationId;
  private LocalDateTime updatedAt;
}
