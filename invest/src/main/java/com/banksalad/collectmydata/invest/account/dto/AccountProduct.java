package com.banksalad.collectmydata.invest.account.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class AccountProduct {

  private String prodType;
  private String prodTypeDetail;
  private String prodCode;
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
  @Builder.Default
  private String currencyCode = "KRW";
}
