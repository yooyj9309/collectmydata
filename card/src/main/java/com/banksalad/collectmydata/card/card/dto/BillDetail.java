package com.banksalad.collectmydata.card.card.dto;

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
public class BillDetail {

  private String cardId;

  private String paidDtime;

  private BigDecimal paidAmt;

  private String currencyCode;

  private String merchantName;

  private BigDecimal creditFeeAmt;

  private int totalInstallCnt;

  private int curInstallCnt;

  private BigDecimal balanceAmt;

  private String prodType;
}
