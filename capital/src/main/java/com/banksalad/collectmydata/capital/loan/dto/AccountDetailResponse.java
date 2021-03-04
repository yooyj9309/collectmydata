package com.banksalad.collectmydata.capital.loan.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.*;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountDetailResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;
  private BigDecimal balanceAmount;  // 대출 잔액
  private BigDecimal loanPrincipal;  // 대출 원금
  private String nextRepayDate;      // 다음 이자 상환일
}
