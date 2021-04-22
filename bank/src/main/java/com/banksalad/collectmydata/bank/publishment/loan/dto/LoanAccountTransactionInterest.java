package com.banksalad.collectmydata.bank.publishment.loan.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanAccountTransactionInterest {

  private String intStartDate;

  private String intEndDate;

  private BigDecimal intRate;

  private String intType;
}
