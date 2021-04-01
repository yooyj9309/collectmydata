package com.banksalad.collectmydata.card.loan.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanLongTerm {

  private String loanDtime;

  private int loanCnt;

  private String loanType;

  private String loanName;

  private BigDecimal loanAmt;

  private BigDecimal intRate;

  private String expDate;

  private BigDecimal balanceAmt;

  private String repayMethod;

  private BigDecimal intAmt;
}
