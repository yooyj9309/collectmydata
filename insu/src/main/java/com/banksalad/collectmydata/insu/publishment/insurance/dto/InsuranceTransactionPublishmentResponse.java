package com.banksalad.collectmydata.insu.publishment.insurance.dto;

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
public class InsuranceTransactionPublishmentResponse {

  private String insuNum;

  private Integer transNo;

  private String transDate;

  private Integer transAppliedMonth;

  private BigDecimal paidAmt;

  private String currencyCode;

  private String payMethod;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
