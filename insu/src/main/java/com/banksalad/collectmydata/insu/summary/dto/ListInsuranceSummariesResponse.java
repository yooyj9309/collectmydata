package com.banksalad.collectmydata.insu.summary.dto;

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
public class ListInsuranceSummariesResponse {

  private String rspCode;

  private String rspMsg;

  private long searchTimestamp;

  private int insuCnt;

  @Builder.Default
  private List<InsuranceSummary> insuList = new ArrayList<>();
}
