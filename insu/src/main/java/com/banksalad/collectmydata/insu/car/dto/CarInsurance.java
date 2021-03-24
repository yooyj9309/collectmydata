package com.banksalad.collectmydata.insu.car.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CarInsurance {

  private String carNumber;
  private String carInsuType;
  private String carName;
  private String startDate;
  private String endDate;
  private String contractAge;
  private String contractDriver;

  @JsonProperty(value = "is_own_dmg_coverage")
  private boolean ownDmgCoverage;

  private String selfPayRate;
  private long selfPayAmt;

  /* additional fields */
  private String insuNum;
  private LocalDateTime transactionSyncedAt;
}
