package com.banksalad.collectmydata.connect.support.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FinanceOrganizationServiceResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
}
