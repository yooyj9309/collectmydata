package com.banksalad.collectmydata.insu.car.dto;

import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetCarInsuranceRequest implements AccountRequest {

  private String orgCode;
  private String insuNum;
  private long searchTimestamp;
}
