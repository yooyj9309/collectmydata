package com.banksalad.collectmydata.insu.car.dto;

import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetCarInsuranceResponse implements AccountResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
  private int carInsuCnt;

  @JsonProperty(value = "car_insu_list")
  private List<CarInsurance> carInsurances;
}
