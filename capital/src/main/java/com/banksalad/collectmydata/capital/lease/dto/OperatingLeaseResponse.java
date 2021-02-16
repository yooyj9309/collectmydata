package com.banksalad.collectmydata.capital.lease.dto;

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
public class OperatingLeaseResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
  private String holderName;
  private String issueDate;
  private String expDate;
  private String repayDate;
  private String repayMethod;
  private String repayOrgCode;
  private String repayAccountNum;
  private String nextRepayDate;
}
