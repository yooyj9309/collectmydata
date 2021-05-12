package com.banksalad.collectmydata.insu.publishment.car.dto;

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
public class CarInsuranceTransactionPublishmentResponse {

  private String insuNum;

  private String carNumber;

  private Integer transNo;

  private BigDecimal faceAmt;

  private BigDecimal paidAmt;

  private String payMethod;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
