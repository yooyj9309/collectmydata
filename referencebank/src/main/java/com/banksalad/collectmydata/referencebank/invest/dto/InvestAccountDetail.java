package com.banksalad.collectmydata.referencebank.invest.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class InvestAccountDetail {

  private String rspCode;

  private String rspMsg;

  private long searchTimestamp;

  @Builder.Default
  private String currencyCode = "KRW";

  private BigDecimal balanceAmt;

  private BigDecimal evalAmt;

  private BigDecimal invPrincipal;

  private BigDecimal fundNum;

}
