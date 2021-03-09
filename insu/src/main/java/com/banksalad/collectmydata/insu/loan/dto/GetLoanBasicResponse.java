package com.banksalad.collectmydata.insu.loan.dto;

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
public class GetLoanBasicResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
  private String loanStartDate;
  private String loanExpDate;
  private String repayMethod;
  private String insuNum;
}
