package com.banksalad.collectmydata.capital.oplease.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OperatingLease {

  private String accountNum;
  private Integer seqno;
  private String holderName;
  private String issueDate;
  private String expDate;
  private Integer repayDate;
  private String repayMethod;
  private String repayOrgCode;
  private String repayAccountNum;
  private String nextRepayDate;
}
