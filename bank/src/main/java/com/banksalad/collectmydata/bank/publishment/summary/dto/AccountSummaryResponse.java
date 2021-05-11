package com.banksalad.collectmydata.bank.publishment.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AccountSummaryResponse {

  private String accountNum;

  private String seqno;

  @JsonProperty("is_consent")
  private boolean consent;

  @JsonProperty("is_foreign_deposit")
  private Boolean foreignDeposit;

  private String prodName;

  private String accountType;

  private String accountStatus;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
