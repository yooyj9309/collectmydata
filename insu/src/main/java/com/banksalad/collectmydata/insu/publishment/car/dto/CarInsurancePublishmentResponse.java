package com.banksalad.collectmydata.insu.publishment.car.dto;

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
public class CarInsurancePublishmentResponse {

  private String insuNum;

  private String carNumber;

  private String carInsuType;

  private String carName;

  private String startDate;

  private String endDate;

  private String contractAge;

  private String contractDriver;

  private Boolean ownDmgCoverage;

  private String selfPayRate;

  private BigDecimal selfPayAmt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
