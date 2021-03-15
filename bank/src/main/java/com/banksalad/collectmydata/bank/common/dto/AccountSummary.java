package com.banksalad.collectmydata.bank.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountSummary {

  private String accountNum;

  @JsonProperty("is_consent")
  private boolean consent;

  private String seqno;

  @JsonProperty("is_foreign_deposit")
  private boolean foreignDeposit;

  private String prodName;

  private String accountType;

  private String accountStatus;

  private long basicSearchTimestamp;

  private String basicSearchResponseCode;

  private long detailSearchTimestamp;

  private String detailSearchResponseCode;

  private LocalDateTime transactionSyncedAt;
}
