package com.banksalad.collectmydata.insu.insurance.dto;

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
public class GetInsurancePaymentResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;

  private String payDue;
  private String payCycle;
  private Integer payCnt;
  private String payOrgCode;
  private String payDate;
  private String payEndDate;
  private BigDecimal payAmt;
  private String currencyCode;
  private Boolean isAutoPay;
}
