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
public class OperatingLeaseBasic {

  private String holderName;        // 고객명
  private String issueDate;         // 대출일              TODO : Type Check
  private String expDate;           // 만기일              TODO : Type Check
  private String repayDate;         // 월상환일
  private String repayMethod;       // 상환 방식 (코드)
  private String repayOrgCode;      // 자동 이체 기관 (코드)
  private String repayAccountNum;   // 상환 계좌 번호 (자동이체)
  private String nextRepayDate;     // 다음 납일 예정일      TODO : Type Check
}
