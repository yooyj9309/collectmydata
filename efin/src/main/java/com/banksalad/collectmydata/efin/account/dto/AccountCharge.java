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
public class AccountCharge {

  private String chargeAccountNum;

  private String chargeOption;

  private String chargeDay;

  private BigDecimal chargeBaseAmt;

  private BigDecimal chargeAmt;

  @Builder.Default
  private String currencyCode = "KRW";
}
