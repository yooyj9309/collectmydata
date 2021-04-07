package com.banksalad.collectmydata.card.template.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
