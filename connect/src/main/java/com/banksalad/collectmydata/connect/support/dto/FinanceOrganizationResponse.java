package com.banksalad.collectmydata.connect.support.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FinanceOrganizationResponse {

  private String rspCode;
  private String rspMsg;
  private Long searchTimestamp;
  private Integer orgCnt;
  private List<FinanceOrganizationInfo> orgList;
}
