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
public class AccountProductResponse {
  private String accountNum;
  private Short prodNo;
  private String prodCode;
  private String prodType;
  private String prodTypeDetail;
  private String prodName;
  private BigDecimal purchaseAmt;
  private Long holdingNum;
  private Long availForSaleNum;
  private BigDecimal evalAmt;
  private String issueDate;
  private BigDecimal paidInAmt;
  private BigDecimal withdrawalAmt;
  private String lastPaidInDate;
  private BigDecimal rcvAmt;
  private String currencyCode;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
