package com.banksalad.collectmydata.mock.invest.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class InvestAccountProduct {

  private String prodType;
  private String prodTypeDetail;
  private String prodCode;
  private String prodName;
  private BigDecimal purchaseAmt;
  private Integer holdingNum;
  private Integer availForSaleNum;
  private BigDecimal evalAmt;
  private LocalDate issueDate;
  private BigDecimal paidInAmt;
  private BigDecimal withdrawalAmt;
  private LocalDate lastPaidInDate;
  private BigDecimal rcvAmt;
  private String currencyCode;
}
