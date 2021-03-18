package com.banksalad.collectmydata.invest.account.dto;

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
public class AccountTransaction {

  private String prodCode;
  private String transDtime;
  private String prodName;
  private String transType;
  private String transTypeDetail;
  private Long transNum;
  private BigDecimal baseAmt;
  private BigDecimal transAmt;
  private BigDecimal settleAmt;
  private BigDecimal balanceAmt;
  private String currencyCode;
}
