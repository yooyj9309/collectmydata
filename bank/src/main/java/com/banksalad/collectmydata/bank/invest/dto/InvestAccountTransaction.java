package com.banksalad.collectmydata.bank.invest.dto;

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
public class InvestAccountTransaction {

  private String transDtime; // 거래일시(YYYYMMDDhhmmss) 또는 거래일자(YYYYMMDD)

  private String transNo;

  private String transType;

  private String currencyCode;

  private BigDecimal baseAmt;

  private BigDecimal transFundNum;

  private BigDecimal transAmt;

  private BigDecimal balanceAmt;

}
