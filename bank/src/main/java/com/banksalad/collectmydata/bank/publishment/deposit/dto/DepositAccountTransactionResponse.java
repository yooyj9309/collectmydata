package com.banksalad.collectmydata.bank.publishment.deposit.dto;

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
public class DepositAccountTransactionResponse {

  private String accountNum;

  private String seqno;

  @Builder.Default
  private String currencyCode = "KRW";

  private String transDtime;

  private String transNo;

  private String transType;

  private String transClass;

  private BigDecimal transAmt;

  private BigDecimal balanceAmt;

  private Integer paidInCnt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
