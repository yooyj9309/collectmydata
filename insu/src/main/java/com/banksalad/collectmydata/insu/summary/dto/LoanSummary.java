package com.banksalad.collectmydata.insu.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanSummary {

  private String prodName;

  private String accountNum;

  @JsonProperty("is_consent")
  private boolean consent;

  private String accountType;

  private String accountStatus;

  private long basicSearchTimestamp;

  private long detailSearchTimestamp;

  private LocalDateTime transactionSyncedAt;
}
