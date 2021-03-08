package com.banksalad.collectmydata.insu.insurance.dto;

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
public class GetInsuranceContractRequest {

  private String orgCode;
  private String insuNum;
  private String insuredNo; // 현재 request명세에는 int로 되어있으나, 실제 받을때는 String으로 받고있는상태
  private long searchTimestamp;
}
