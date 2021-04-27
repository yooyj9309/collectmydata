package com.banksalad.collectmydata.invest.publishment.account.dto;

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
public class AccountBasicResponse {
  private String accountNum;
  private String issueDate;
  private Boolean taxBenefits;
  private BigDecimal withholdingsAmt;
  private BigDecimal creditLoanAmt;
  private BigDecimal mortgageAmt;
  private String currencyCode;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
