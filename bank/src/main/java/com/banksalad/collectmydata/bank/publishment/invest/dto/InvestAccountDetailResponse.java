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
public class InvestAccountDetailResponse {

  private String accountNum;

  private String seqno;

  private String currencyCode;

  private BigDecimal balanceAmt;

  private BigDecimal evalAmt;

  private BigDecimal invPrincipal;

  private BigDecimal fundNum;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
