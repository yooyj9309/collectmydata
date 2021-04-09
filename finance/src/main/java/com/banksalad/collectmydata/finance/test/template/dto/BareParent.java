package com.banksalad.collectmydata.finance.test.template.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class BareParent {

  private LocalDateTime syncedAt;

  private boolean consents;

  private long searchTimestamp;

  private String responseCode;

  private LocalDateTime transactionAt;

  private String transactionResponseCode;
}
