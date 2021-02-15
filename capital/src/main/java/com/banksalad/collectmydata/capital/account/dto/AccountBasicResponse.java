package com.banksalad.collectmydata.capital.account.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountBasicResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
  private String holderName;
  private String issueDate; // TODO : execute 시 LocalDate 변환하여 받을 수 있는지 확인
  private String expDate; // TODO : execute 시 LocalDate 변환하여 받을 수 있는지 확인
  private BigDecimal lastOfferedRate;
  private String repayDate;
  private String repayMethod;
  private String repayOrgCode;
  private String repayAccountNum;
}