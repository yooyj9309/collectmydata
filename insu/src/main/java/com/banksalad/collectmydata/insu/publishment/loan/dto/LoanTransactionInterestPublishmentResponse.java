package com.banksalad.collectmydata.insu.publishment.loan.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanTransactionInterestPublishmentResponse {

  private String accountNum;

  private String transDtime;

  private String transNo;

  private Integer intNo;

  private String intStartDate;

  private String intEndDate;

  private BigDecimal intRate;

  private String intType;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
