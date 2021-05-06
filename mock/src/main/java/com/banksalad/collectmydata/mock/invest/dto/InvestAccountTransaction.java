package com.banksalad.collectmydata.mock.invest.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class InvestAccountTransaction {

  private String prodName;
  private String prodCode;
  private String transDtime;
  private String transType;
  private String transTypeDetail;
  private Integer transNum;
  private BigDecimal baseAmt;
  private BigDecimal transAmt;
  private BigDecimal settleAmt;
  private BigDecimal balanceAmt;
  private String currencyCode;
}
