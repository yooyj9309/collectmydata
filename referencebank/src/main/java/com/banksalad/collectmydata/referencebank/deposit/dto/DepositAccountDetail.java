package com.banksalad.collectmydata.referencebank.deposit.dto;

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
public class DepositAccountDetail {

  private String currencyCode;

  private BigDecimal balanceAmt;

  private BigDecimal withdrawableAmt;

  private BigDecimal offeredRate;

  private Integer lastPaidInCnt;

}
