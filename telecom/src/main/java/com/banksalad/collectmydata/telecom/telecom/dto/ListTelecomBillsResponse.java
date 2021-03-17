package com.banksalad.collectmydata.telecom.telecom.dto;

import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
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
public class ListTelecomBillsResponse implements AccountResponse {

  private String rspCode;
  private String rspMsg;
  private int billCnt;
  private long searchTimestamp; // 사용하진않으나, AccountResponse를 위해 생성

  @Builder.Default
  private List<TelecomBill> billList = new ArrayList<>();
}
