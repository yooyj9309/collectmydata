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
public class LoanDetailPublishmentResponse {

  private String accountNum;

  private String currencyCode;

  private BigDecimal balanceAmt;

  private BigDecimal loanPrincipal;

  private String nextRepayDate;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
