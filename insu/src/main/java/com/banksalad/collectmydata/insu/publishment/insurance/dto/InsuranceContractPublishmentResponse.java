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
public class InsuranceContractPublishmentResponse {

  private String insuNum;

  private String insuredNo;

  private Integer contractNo;

  private String contractStatus;

  private String contractName;

  private String contractExpDate;

  private BigDecimal contractFaceAmt;

  private String currencyCode;

  private Boolean required;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
