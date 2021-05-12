package com.banksalad.collectmydata.insu.publishment.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class InsurancePaymentPublishmentResponse {

  private String insuNum;

  private String payDue;

  private String payCycle;

  private Integer payCnt;

  private String payOrgCode;

  private String payDate;

  private String payEndDate;

  private BigDecimal payAmt;

  private String currencyCode;

  @JsonProperty("is_auto_pay")
  private Boolean autoPay;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
