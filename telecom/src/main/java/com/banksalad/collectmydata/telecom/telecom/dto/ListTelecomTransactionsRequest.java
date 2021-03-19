package com.banksalad.collectmydata.telecom.telecom.dto;

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
public class ListTelecomTransactionsRequest {

  private String orgCode;
  private String mgmtId;
  private String fromMonth; // TODO : telecom 에서는 날짜를 정수형 N으로 받고있어 수정 될 수 있음
  private String toMonth;
}
