package com.banksalad.collectmydata.invest.template.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class BareParent {

  private LocalDateTime syncedAt;

  private long searchTimestamp;

  private String responseCode;

  private LocalDateTime transactionAt;

  private String transactionResponseCode;
}
