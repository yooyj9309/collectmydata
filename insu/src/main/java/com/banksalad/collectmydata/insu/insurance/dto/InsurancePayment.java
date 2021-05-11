package com.banksalad.collectmydata.insu.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InsurancePayment {

  private String payDue;
  private String payCycle;
  private Integer payCnt;
  private String payOrgCode;
  private String payDate;
  private String payEndDate;
  private BigDecimal payAmt;
  private String currencyCode;

  @JsonProperty("is_auto_pay")
  private boolean autoPay;
}
