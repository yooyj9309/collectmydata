package com.banksalad.collectmydata.efin.account.dto;

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
public class AccountBalance {

  private String fobName;
  private BigDecimal totalBalanceAmt;
  private BigDecimal chargeBalanceAmt;
  private BigDecimal reserveBalanceAmt;
  private BigDecimal reserveDueAmt;
  private BigDecimal expDueAmt;
  private BigDecimal limitAmt;
}
