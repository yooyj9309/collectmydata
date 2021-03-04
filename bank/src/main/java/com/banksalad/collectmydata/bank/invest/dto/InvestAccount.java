package com.banksalad.collectmydata.bank.invest.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InvestAccount {

  private String accountNum;
  private String seqno;
  private String standardFundCode;
  private String paidInType;
  private String issueDate;
  private String expDate;
  private String currencyCode;
  private BigDecimal balanceAmt;
  private BigDecimal evalAmt;
  private BigDecimal invPrincipal;
  private BigDecimal fundNum;
}
