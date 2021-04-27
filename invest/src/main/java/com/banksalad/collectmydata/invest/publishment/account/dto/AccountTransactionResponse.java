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
public class AccountTransactionResponse {
  private String accountNum;
  private String uniqueTransNo;
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
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
