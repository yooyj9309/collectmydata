package com.banksalad.collectmydata.bank.publishment.invest.dto;

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
public class InvestAccountTransactionResponse {

  private String accountNum;

  private String seqno;

  private String currencyCode;

  private String transDtime;

  private String transNo;

  private String transType;

  private BigDecimal baseAmt;

  private BigDecimal transFundNum;

  private BigDecimal transAmt;

  private BigDecimal balanceAmt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
