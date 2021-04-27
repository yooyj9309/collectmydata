package com.banksalad.collectmydata.invest.publishment.summary.dto;

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
  private Boolean consent;
  private String accountName;
  private String accountType;
  private String accountStatus;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
