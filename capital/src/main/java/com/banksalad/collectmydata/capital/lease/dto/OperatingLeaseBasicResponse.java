package com.banksalad.collectmydata.capital.lease.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OperatingLeaseBasicResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
  private String holderName;        // 고객명
  private String issueDate;         // 대출일              TODO : Type Check
  private String expDate;           // 만기일              TODO : Type Check
  private String repayDate;         // 월상환일
  private String repayMethod;       // 상환 방식 (코드)
  private String repayOrgCode;      // 자동 이체 기관 (코드)
  private String repayAccountNum;   // 상환 계좌 번호 (자동이체)
  private String nextRepayDate;     // 다음 납일 예정일      TODO : Type Check
}
